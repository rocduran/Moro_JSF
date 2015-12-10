package ad.uda.moro;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Valoracio;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

@ManagedBean(name = "valoracioMBean")
@SessionScoped
public class ValoracioMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to
	private String errorMessage = " "; // Messages

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Moro entities:
	private List<Valoracio> valoracioList;
	private Valoracio selectedValoracio;

	public ValoracioMBean() {
		WebOperations.log("ValoracioMBean: Default constructor called");

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

		// Load the Valoracio list:
		this.loadValoracions();
	}

	public String loadValoracions() {

		WebOperations.log("EnquestesServiceMBean: loadValoracions() called.");

		try {
			Valoracio[] valoracions = this.enquestesService.getValoracioList();
			this.valoracioList = Arrays.asList(valoracions);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}
	
	public String loadValoracionsByIdServei(int idServei) {

		WebOperations.log("EnquestesServiceMBean: loadValoracions() called.");

		try {
			Valoracio[] valoracions = this.enquestesService.getValoracioServei(idServei);
			this.valoracioList = Arrays.asList(valoracions);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return null;
		}
		return null;
	}

	public Valoracio getSelectedValoracio() {
		return selectedValoracio;
	}

	public void setSelectedValoracio(Valoracio selectedValoracio) {
		this.selectedValoracio = selectedValoracio;

	}

	public List<Valoracio> getValoracioList() {
		return valoracioList;
	}
	
	public List<Valoracio> getValoracioList(String idServei) {
		int id = Integer.valueOf(idServei);
		loadValoracionsByIdServei(id);
		return valoracioList;
	}

	public void setValoracioList(List<Valoracio> valoracioList) {
		this.valoracioList = valoracioList;
	}
}