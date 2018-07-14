package CPN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "general")
@XmlAccessorType(XmlAccessType.FIELD)
public class General {
	public static float lambda_s0 = 0.9f;
	public static float lambda_c0 = 0.9f;
	public static float snr = 9.0f;
	public static int mmax = 213900;
	public static Map<String, Object> params = new HashMap<String, Object>();
	public static Map<Integer, ArrayList<Integer>> infoMaping = new HashMap<Integer, ArrayList<Integer>>();

	@XmlElement
	public float getLambda_s0() {return this.lambda_s0;}
	@XmlElement
	public float getLambda_c0() {return this.lambda_c0;}
	@XmlElement
	public float getSnr() {return this.snr;}
	@XmlElement
	public int getMmax() {return this.mmax;}
}