package Clustering.CPN;

public class Channel {
	private int from;
	private int to;
	private int id;
	private int cb;
	private int cbmax;
	private double mu_t;
	private double sigma_t;

	public Channel(int from, int to, int id, int cb, int cbmax, double mu_t, double sigma_t){
		this.from = from;
		this.to = to;
		this.id = id;
		this.cb = cb;
		this.cbmax = cbmax;
		this.mu_t = mu_t;
		this.sigma_t = sigma_t;
	}

	public int getId() {return this.id;}
	public int getFrom() {return this.from;}
	public int getTo() {return this.to;}
	public int getCb() {return this.cb;}
	public int getCbmax() {return this.cbmax;}
	public double getmu_t() {return this.mu_t;}
	public double getsigma_t() {return this.sigma_t;}

	public void setCb(int cb) {this.cb = cb;}
	public void setCbmax(int cb) {this.cbmax = cb;}
	public void setId(int id) {this.id = id;}
	public void setFrom(int from) {this.from = from;}
	public void setTo(int to) {this.to = to;}

	public Channel clone(){
		return new Channel(this.from, this.to, this.id, this.cb, this.cbmax, this.mu_t, this.sigma_t);
	}

	public boolean equals(CPN.Channel c){
		return this.from == c.getFrom() && this.to == c.getTo() && this.id == c.getId() && this.cb == c.getCb() &&
				this.cbmax == c.getCbmax() && this.mu_t == c.getmu_t() && this.sigma_t == c.getsigma_t();
	}

	@Override
	public String toString() {
		String sc = "id: " + this.id+", cb: "+this.cb+", cbmax: "+this.cbmax+"\n";
		return sc;
	}
}