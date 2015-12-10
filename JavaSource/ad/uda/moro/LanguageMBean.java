package ad.uda.moro;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * This managed bean class controls the local settings and the language switching in the application<p/>
 * This class is declared "Managed Bean" with the <code>@ManagedBean</code> annotation.
 * The reference to this bean in xhtml pages is specified in the ManagedBean name attribute.
 * In this case, the name is thus "<b>languageSelector</b>".
 * @author Manfred Geiler modified by Alexander Beening: Added the <code>@ManagedBean</code> annotation.
 */
@ManagedBean(name = "languageMBean")
@SessionScoped
public class LanguageMBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Supported locales:
	private static final Locale english = Locale.ENGLISH;
	private static final Locale catalan = new Locale("ca", "", "");
	private static final Locale spanish = new Locale("es", "", "");
	private Locale currentLocale;
	
	private WebOperations operations = null;

	/**
	 * Constructor. Sets the default language.
	 */
	public LanguageMBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getViewRoot().setLocale(catalan);
		this.setCurrentLocale(catalan);

		// Obtain the web operations instance which gives access to common context and data:
		operations = WebOperations.getInstance(); // Get the WebOperations instance
		if (!operations.isInitialized()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application not initialized", null));
			return;
		}
	}
	/**
	 * Returns the currently active locale
	 * @return an object of class Locale.
	 */
	public Locale getCurrentLocale() {
		return currentLocale;
	}
	public void setCurrentLocale(Locale currentLocale) {
		this.currentLocale = currentLocale;
	}

	/**
	 * Switches to the language, selected by clicking at an image.
	 * @param event The <b>ActionEvent</b> instance
	 */
	public void chooseLocaleFromLink(ActionEvent event) {
		
		// Get Event and FacesContext information:
		String current = event.getComponent().getId();
		FacesContext context = FacesContext.getCurrentInstance();

		// Select the Locale according to the selected language
		if (current.startsWith("eng")) { // English
			context.getViewRoot().setLocale(english);
			this.setCurrentLocale(english);
			return;
		}

		if (current.startsWith("cat")) { // Catalan
			context.getViewRoot().setLocale(catalan);
			this.setCurrentLocale(catalan);
			return;
		}
		
		if (current.startsWith("es")) { // Espanyol
			context.getViewRoot().setLocale(spanish);
			this.setCurrentLocale(spanish);
			return;
		}
	}

}
