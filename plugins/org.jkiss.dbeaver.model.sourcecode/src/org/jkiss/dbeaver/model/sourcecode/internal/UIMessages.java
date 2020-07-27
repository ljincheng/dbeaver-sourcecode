package org.jkiss.dbeaver.model.sourcecode.internal;

import org.eclipse.osgi.util.NLS;

public class UIMessages  extends NLS {
	static final String BUNDLE_NAME = "org.jkiss.dbeaver.model.sourcecode.internal.UIMessages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
	}

	private UIMessages() {
	}

	public static String dbeaver_generate_sourcecode_settings;
	public static String dbeaver_generate_sourcecode_codeOutPutFolder;
	public static String dbeaver_generate_sourcecode_packageName;
	public static String dbeaver_generate_sourcecode_camelCaseNames;
	public static String dbeaver_generate_sourcecode_refresh;
	public static String dbeaver_generate_sourcecode_save;
	public static String dbeaver_generate_sourcecode_msg_selectOutPutFolder;
	public static String dbeaver_generate_sourcecode_msg_selectOutPutFolder_detailTip;
	public static String dbeaver_generate_sourcecode_error;
	public static String dbeaver_generate_sourcecode_generatorSourceCodeError;
	public static String dbeaver_generate_sourcecode_pageClassFullName;
	public static String dbeaver_generate_sourcecode_entitySuffix;
	public static String dbeaver_generate_sourcecode_groupName;
	
	public static String dbeaver_generate_sourcecode_preferences_generalSetting;
	public static String dbeaver_generate_sourcecode_preferences_generalFileRule;
	public static String dbeaver_generate_sourcecode_preferences_author;
	public static String dbeaver_generate_sourcecode_preferences_templateParamName;

}
