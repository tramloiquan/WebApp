package CPN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class Channel {
	@XmlElement
	private int id;
	@XmlElement
	private int from;
	@XmlElement
	private int to;
	@XmlElement(name = "buffer")
	private int cb;
	@XmlElement(name = "buffer_size")
	private int cbmax;
	@XmlElement
	private float mu_t;
	@XmlElement
	private float sigma_t;

	public Channel(){}
	public Channel(int from, int to, int id, int cb, int cbmax, float mu_t, float sigma_t){
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
	public float getmu_t() {return this.mu_t;}
	public float getsigma_t() {return this.sigma_t;}
	public void setCb(int cb) {this.cb = cb;}
	public void setCbmax(int cb) {this.cbmax = cb;}
	public void setId(int id) {this.id = id;}
	public void setFrom(int from){this.from = from;}
	public void setTo(int to){this.to = to;}
	
	public Channel clone(){
		return new Channel(this.from, this.to, this.id, this.cb, this.cbmax, this.mu_t, this.sigma_t);
	}

	public boolean equals(Channel c){
	    return this.from == c.getFrom() && this.to == c.getTo() && this.id == c.getId() && this.cb == c.getCb() &&
                this.cbmax == c.getCbmax() && this.mu_t == c.getmu_t() && this.sigma_t == c.getsigma_t();
    }

	@Override
	public String toString() {
		String sc = "id: " + this.id+", cb: "+this.cb+", cbmax: "+this.cbmax+"\n";
		return sc;
	}
}