package models.metamap.model;

import java.util.ArrayList;
import java.util.List;

public class MetamapValues {
	private List<MetamapValue> metamapValues;

    public MetamapValues() {
        this.metamapValues = new ArrayList<MetamapValue>();
    }

    public MetamapValues(List<MetamapValue> metamapvalues) {
        this.metamapValues = metamapvalues;
    }

    public List<MetamapValue> getMetamapValues() {
        return metamapValues;
    }

    public void setMetamapValues(List<MetamapValue> metamapValues) {
        this.metamapValues = metamapValues;
    }
    
    public void clearList() {
    	metamapValues.clear();    	
    }
}
