package pojos;

public class ClientAttributes {
	
	private String id;
	private int table;
	
	public ClientAttributes() {
		super();
		id="";
		table=0;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTable() {
		return table;
	}
	public void setTable(int table) {
		this.table = table;
	}
}
