package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

public class testCreateNewProjectAndDelete extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestCreateNewProjectAndDelete() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new TreeItemLocator("Java"));
		ui.enterText("+");
		ui.click(new TreeItemLocator("Java/Package Explorer"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
		ui.click(new MenuItemLocator("File/New/Other..."));
		ui.wait(new ShellShowingCondition("New"));
		ui.click(new TreeItemLocator("Java"));
		ui.enterText("+");
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("JP");
		// ?
		// ? These lines are generated by Eclipse 3.1 and 3.2
		ui                                                                // ? Eclipse 3.1 and 3.2
			.click(new ButtonLocator(                                     // ? Eclipse 3.1 and 3.2
				"&Create separate source and output folders"));           // ? Eclipse 3.1 and 3.2
		// ?
		// ? These lines are generated by Eclipse 3.3
		ui.click(new ButtonLocator(                                       // ? Eclipse 3.3
				"&Create separate folders for sources and class files")); // ? Eclipse 3.3
		// ?
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.contextClick(new TreeItemLocator("JP", new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "Delete");
		ui.wait(new ShellShowingCondition("Confirm Project Delete"));
		ui.click(new ButtonLocator("&No"));
		ui.wait(new ShellDisposedCondition("Confirm Project Delete"));
	}

}