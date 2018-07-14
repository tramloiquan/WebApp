package Clustering.CPN;

public class Sensor{
	private float x;
	private float y;
	private int id;
	private int type;
	private int b;
	private int q;
	private int sbmax;
	private int sqmax;
	private float energy;
	private float originalEnergy;
	private float mu_p;
	private float sigma_p;
	private float mu_s;
	private float sigma_s;

	public Sensor(float x, float y, int id, int type, int b, int q, int sbmax, int sqmax, float energy, float originalEnergy, float mu_p, float sigma_p, float mu_s, float sigma_s){
		this.x = x;
		this.y = y;
		this.id = id;
		this.type = type;
		this.b = b;
		this.q = q;
		this.sbmax = sbmax;
		this.sqmax = sqmax;
		this.energy = energy;
		this.originalEnergy = originalEnergy;
		this.mu_p = mu_p;
		this.sigma_p = sigma_p;
		this.mu_s = mu_s;
		this.sigma_s = sigma_s;
	}
	public float getX(){return this.x;}
	public float getY(){return this.y;}
	public int getId() {return this.id;}
	public int getType() {return this.type;}
	public int getQ()  {return this.q;}
	public int getB()  {return this.b;}
	public int getSqmax() {return this.sqmax;}
	public int getSbmax() {return this.sbmax;}
	public float getEnergy() {return this.energy;}
	public float getOriginalEnergy(){return this.originalEnergy;}
	public float getmu_p() {return this.mu_p;}
	public float getsigma_p() {return this.sigma_p;}
	public float getmu_s() {return this.mu_s;}
	public float getsigma_s() {return this.sigma_s;}


	public void setId(int id) {this.id = id;}
	public void setQ(int q) {this.q = q;}
	public void setB(int b) {this.b = b;}
	public void setEnergy(float e) {this.energy = e;}
	public void setSqmax(int sqmax) {this.sqmax = sqmax;}

	public Sensor clone(){
		return new Sensor(this.x, this.y, this.id, this.type, this.b, this.q, this.sbmax, this.sqmax, this.energy, this.originalEnergy, this.mu_p, this.sigma_p, this.mu_s, this.sigma_s);
	}

	public boolean equals(Sensor s){
		return this.id == s.getId() && this.type == s.getType() && this.b == s.getB() && this.q == s.getQ() && this.energy == s.getEnergy() &&
				this.originalEnergy == s.getOriginalEnergy() && this.mu_p == s.getmu_p() && this.sigma_p == s.getsigma_p() &&
				this.mu_s == s.getmu_s() && this.sigma_s == s.getsigma_s();
	}

	@Override
	public String toString() {
		String ss = "id: " + this.id+ ", b: "+this.b+", q: "+this.q+", sbmax: "+this.sbmax+", sqmax: "+this.sqmax+", energy: "+this.energy+"\n";
		return ss;
	}
}