package study.filemoniter;


public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args) {
		FileMoniter subject = new FileMoniter();
		FileObserver observer = new FileObserver();
		subject.addObserver(observer);
		subject.addFile("F:/test/TestHttp/src/HttpRequest.java");
		subject.run();
	}

}
