// 임시 소스 파일
// notifyEmptySeat 실행 메인 코드

package notifyEmptySeat;

public class MainApp {
	public static void main(String[] args) {
		String filePath = "seats.txt";
		SeatManager seatManager = new SeatManager(filePath);
		FileMonitor fileMonitor = new FileMonitor(filePath, seatManager);
		Thread monitorThread = new Thread(fileMonitor);
		// 데몬 -> 프로그램 종료 시에 같이 끝
		monitorThread.setDaemon(true);
		monitorThread.start();
		// 시작
		System.out.println("================================================");
		System.out.println("  빈자리 알림 시스템이 정상 가동되었습니다.  ");
		System.out.println("  seats.txt 파일을 수정하면 알림이 발생합니다.       ");
		System.out.println("  종료하려면 콘솔창에서 'Enter'키를 누르세요.         ");
		System.out.println("================================================");

		try {
			// 종료 안되게 대기
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("프로그램을 안전하게 종료합니다.");
	}
}
