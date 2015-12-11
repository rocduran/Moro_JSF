package ad.uda.moro;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;	
import javax.faces.context.FacesContext;

import com.sun.javafx.collections.MappingChange.Map;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Parametre;
import ad.uda.moro.ejb.entity.Servei;
import ad.uda.moro.ejb.entity.Valoracio;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

@ManagedBean(name = "serveiMBean")
@ApplicationScoped
public class ServeiMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to
	private String errorMessage = " "; // Messages

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Moro entities:
	private List<Servei> serveiList;
	private Servei selectedServei;

	public ServeiMBean() {
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
		this.loadServeis();

	}

	private String loadServeis() {

		WebOperations.log("EnquestesServiceMBean: loadServeis() called.");

		try {
			Servei[] serveiArray = this.enquestesService.getServeiList();
			System.out.println("Load Servei: " + serveiArray);
			this.serveiList = Arrays.asList(serveiArray);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}

	public List<Servei> getServeiList() {
		return serveiList;
	}

	public void setServeiList(List<Servei> serveiList) {
		this.serveiList = serveiList;
	}

	public Servei getSelectedServei() {
		return selectedServei;
	}

	public void setSelectedServei(Servei selectedServei) {
		this.selectedServei = selectedServei;
	}

	public Servei getServeiByIdFromList(int id) {
		for (int i = 0; i < this.serveiList.size(); i ++){
			if(this.serveiList.get(i).getId() == id ) return this.serveiList.get(i);
		}
		return null;
	}
	
//	public int getMitjanaParametre(String param){
//		if (selectedServei == null) return -1;
//		 
//		int idParam = Integer.valueOf(param);
//		try {
//			Valoracio[] valoracions = this.enquestesService.getValoracioList();
//			int suma = 0;
//			int total = 0;
//			for (int i = 0; i < valoracions.length; i++){
//				if(valoracions[i].getIdParam().getId() == idParam && valoracions[i].getIdServei().getId() == selectedServei.getId() ){
//					suma += valoracions[i].getValor();
//					total ++;
//				}
//			}
//			if( total == 0) return 0;
//			return suma / total;
//		} catch (MoroException ex) {
//			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
//			WebOperations.addErrorMessage(errorMessage);
//			return -1;
//		}
//	}

}