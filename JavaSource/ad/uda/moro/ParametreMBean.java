package ad.uda.moro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Parametre;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

@ManagedBean(name = "parametreMBean")
@SessionScoped
public class ParametreMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to
	private String errorMessage = " "; // Messages

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Moro entities:
	private List<Parametre> parametreList;
	private Parametre selectedParametre;

	public ParametreMBean() {
		WebOperations.log("ServeiMBean: Default constructor called");

		// Obtain the web operations instance which gives access to common
		// context and data:
		this.operations = WebOperations.getInstance();
		if (!operations.isInitialized()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application not initialized", null));
			this.errorMessage = "Application not initialized";
			return;
		}

		// Get all references to EJB session beans:
		this.enquestesService = operations.getEnquestesServiceRemote();

		// Load the Servei list:
		this.loadParametres();
		
	}

	private String loadParametres() {

		WebOperations.log("ParametreMBean: loadParametre() called.");

		try {
			Parametre[] parametres = this.enquestesService.getParametreList();
			this.parametreList = Arrays.asList(parametres);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}

	public List<Parametre> getParametreList() {
		return parametreList;
	}

	public void setParametreList(List<Parametre> parametreList) {
		this.parametreList = parametreList;
	}

	public Parametre getSelectedParametre() {
		return selectedParametre;
	}

	public void setSelectedParametre(Parametre selectedParametre) {
		this.selectedParametre = selectedParametre;
	}
	
	public List<Parametre> getParametreList(int idServei){
		List<Parametre> parametresByIdTipus = new ArrayList<Parametre>();
		
		int id = Integer.valueOf(idServei);
		for (int i = 0; i < parametreList.size(); i++){
			if(parametreList.get(i).getIdTipus() == idServei){
				parametresByIdTipus.add(parametreList.get(i));
			}
		}
		return parametresByIdTipus;
	}
}
