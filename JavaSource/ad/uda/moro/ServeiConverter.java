package ad.uda.moro;
 
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import ad.uda.moro.ejb.entity.Servei;
  
@FacesConverter("serveiConverter")
public class ServeiConverter implements Converter {
 
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
            	ServeiMBean service = (ServeiMBean) fc.getExternalContext().getApplicationMap().get("selectOneListBoxBean");
                return service.getServeiList().get((Integer.parseInt(value)));
            } catch(NumberFormatException e) {
                throw new ConverterException();
            }
        }
        else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Servei) object).getId());
        }
        else {
            return null;
        }
    }   
}