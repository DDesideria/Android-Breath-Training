package name.freediving.entry;


/**
 * Holds one instance of prefs in its structure Name: name of pref <br>
 * count : number of counts <br>
 * tBrIn=a+b*count <br>
 * tHIn=c+d*count <br>
 * tBrOut=e+f*count <br>
 * tHOut=g+h*count <br>
 */
public class TrainingPrimitive {
	@Override
	public String toString() {
		return "TrainingPrimitive [a=" + a + ", b=" + b + ", c=" + c
				+ ", count=" + count + ", d=" + d + ", e=" + e + ", f=" + f
				+ ", g=" + g + ", h=" + h + ", id=" + id + ", name=" + name
				+ ", ordering=" + ordering + ", progID=" + progID + ", rest="
				+ rest + ", type=" + type + "]";
	}

	final static int NORMAL = 0;

	private int a;
	private int b;
	private int c;
	private int count;
	private int d;
	private int e;
	private int f;
	private int g;
	private int h;
	private long id = -1;
	private String name;
	private int ordering;
	private long progID;
	private int rest;
	private int type;

	public TrainingPrimitive() {
		setA(0);
		setB(0);
		setC(0);
		setD(0);
		setName("");
		setE(0);
		setF(0);
		setG(0);
		setH(0);
		setCount(0);
		setRest(0);
		setType(NORMAL);

	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int getC() {
		return c;
	}

	public int getCount() {
		return count;
	}

	public int getD() {
		return d;
	}

	public int getE() {
		return e;
	}

	public int getF() {
		return f;
	}

	public int getG() {
		return g;
	}

	public int getH() {
		return h;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getOrdering() {
		return ordering;
	}

	public long getProgID() {
		return progID;
	}

	public int getRest() {
		return rest;
	}

	public int getType() {
		return type;
	}

	public void setA(int a) {
		this.a = a;
	}

	public void setB(int b) {
		this.b = b;
	}

	public void setC(int c) {
		this.c = c;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setD(int d) {
		this.d = d;
	}

	public void setE(int e) {
		this.e = e;
	}

	public void setF(int f) {
		this.f = f;
	}

	public void setG(int g) {
		this.g = g;
	}

	public void setH(int h) {
		this.h = h;
	}

	public void setId(long id2) {
		this.id = id2;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}

	public void setProgID(long progID) {
		this.progID = progID;
	}

	public void setRest(int rest) {
		this.rest = rest;
	}

	public void setType(int type) {
		this.type = type;
	}
}
