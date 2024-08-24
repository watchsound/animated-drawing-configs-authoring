package python;

import java.util.ArrayList;
import java.util.List;

public class tuple {

	List<String> values = new ArrayList<>();
	
	public tuple() {}
	public tuple(String left, String right) {
	   values.add(left);
	   values.add(right);
	}
	
	public tuple(String... values) {
		for(String v : values)
			this.values.add(v);
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	 
	
}
