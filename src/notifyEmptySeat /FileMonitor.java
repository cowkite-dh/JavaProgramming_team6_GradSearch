// 

package notifyEmptySeat;

import java.io.IOException;
import java.nio.file.*;

public class FileMonitor implements Runnable {
	private final String targetFileName; // 감시할 파일 이름
	private final Path dirPath; // 감시할 파일의 경로
	private final SeatManager seatManager; // 변화 감지할 SeatManager

	// 생성자
	public FileMonitor(String filePath, SeatManager seatManager) {
		Path path = Paths.get(filePath).toAbsolutePath(); // 주소 뽑기
		this.targetFileName = path.getFileName().toString(); // 전체 주소에서 파일 이름 뽑기
		this.dirPath = path.getParent(); // 파일이 들어있는 위치(부모폴더 위치)
		this.seatManager = seatManager; // SeatManager 객체 받기
	}

	// run 오버라이딩 - 스레드 켜지면 감시
	@Override
	public void run() {
		try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
			// 감시할 폴더(dirPath)에 파일이 수정되는지 보기
			dirPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			System.out.println("감시 시작: " + dirPath + " 폴더 안의 " + targetFileName);
			// 프로그램 꺼지기 전까지 계속 감시
			while (!Thread.currentThread().isInterrupted()) {
				// 걸릴때까지 대기
				WatchKey key = watchService.take();
				// 걸리면 하나하나 조사
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind(); // 종류 묻기
					// 노이즈 신호(오버플로우)라면 무시
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}
					// 어떤 파일이 바뀐 건지 보기
					Path context = (Path) event.context();
					// 바뀐 파일이 있고, 그 파일 이름이targetFileName("seats.txt")이 맞으면
					if (context != null && context.toString().equals(targetFileName)) {
						System.out.println("\n" + targetFileName + " 파일이 수정됨");
						// 파일 저장 기다리기
						Thread.sleep(100);
						// SeatManager로 뭐가 바뀐지
						seatManager.checkChanges();
					}
				}
				// 데이터 키 리셋 - 대기상태
				boolean valid = key.reset();
				if (!valid) { // 키가 유효하지 않으면 작동 정지
					break;
				}
			}
		} catch (IOException e) {
			System.err.println("감시 오류 발생: " + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("감시 종료");
			Thread.currentThread().interrupt(); // 스레드를 종료 상태로 변경
		}
	}
}
