package server;

public enum TasksDB {

	SomethingDefault(0), notifyUsers(1);
	
	private final int operation_ID;
	private TasksDB(int x) {
		this.operation_ID = x;
	}
	
	public int x() {
		return operation_ID;
	}
	
}
