package com.msgumball.dev.json;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new anywheresoftware.b4a.ShellBA(this.getApplicationContext(), null, null, "com.msgumball.dev.json", "com.msgumball.dev.json.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.msgumball.dev.json", "com.msgumball.dev.json.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.msgumball.dev.json.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }



public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}
public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}

private static BA killProgramHelper(BA ba) {
    if (ba == null)
        return null;
    anywheresoftware.b4a.BA.SharedProcessBA sharedProcessBA = ba.sharedProcessBA;
    if (sharedProcessBA == null || sharedProcessBA.activityBA == null)
        return null;
    return sharedProcessBA.activityBA.get();
}
public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = killProgramHelper(main.mostCurrent == null ? null : main.mostCurrent.processBA);
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.collections.Map _msgmission = null;
public static String _msgmissionjsontest = "";
public static String _msgdata = "";
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _fdicon = null;
public static String _jsonselected = "";
public de.donmanfred.FilePickerDialogWrapper _fp = null;
public de.donmanfred.DialogPropertiesWrapper _props = null;
public de.donmanfred.FileListItemWrapper _item = null;
public static int _single_mode = 0;
public static int _multi_mode = 0;
public static int _file_select = 0;
public static int _dir_select = 0;
public static int _file_and_dir_select = 0;
public static void  _activity_create(boolean _firsttime) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_create", false))
	 {Debug.delegate(mostCurrent.activityBA, "activity_create", new Object[] {_firsttime}); return;}
ResumableSub_Activity_Create rsub = new ResumableSub_Activity_Create(null,_firsttime);
rsub.resume(processBA, null);
}
public static class ResumableSub_Activity_Create extends BA.ResumableSub {
public ResumableSub_Activity_Create(com.msgumball.dev.json.main parent,boolean _firsttime) {
this.parent = parent;
this._firsttime = _firsttime;
}
com.msgumball.dev.json.main parent;
boolean _firsttime;
String[] _ext = null;
String _pattern = "";
String _jsontext = "";
String _filepath = "";
String _filename = "";
anywheresoftware.b4a.keywords.Regex.MatcherWrapper _matcher1 = null;
int _g = 0;
anywheresoftware.b4a.objects.collections.Map _jsonmap = null;
int step25;
int limit25;

@Override
public void resume(BA ba, Object[] result) throws Exception{
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=131073;
 //BA.debugLineNum = 131073;BA.debugLine="If FirstTime Then";
if (true) break;

case 1:
//if
this.state = 4;
if (_firsttime) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=131074;
 //BA.debugLineNum = 131074;BA.debugLine="FilePickerInitialize";
_filepickerinitialize();
 if (true) break;

case 4:
//C
this.state = 5;
;
RDebugUtils.currentLine=131076;
 //BA.debugLineNum = 131076;BA.debugLine="Dim ext() As String";
_ext = new String[(int) (0)];
java.util.Arrays.fill(_ext,"");
RDebugUtils.currentLine=131077;
 //BA.debugLineNum = 131077;BA.debugLine="ext  = Array As String (\"json\")";
_ext = new String[]{"json"};
RDebugUtils.currentLine=131078;
 //BA.debugLineNum = 131078;BA.debugLine="props.Initialize(\"\",File.DirDefaultExternal & \"/M";
parent.mostCurrent._props.Initialize(processBA,"",anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal()+"/MSGData",parent._single_mode,parent._file_select,_ext);
RDebugUtils.currentLine=131079;
 //BA.debugLineNum = 131079;BA.debugLine="item.Initialize(\"\")";
parent.mostCurrent._item.Initialize(processBA,"");
RDebugUtils.currentLine=131080;
 //BA.debugLineNum = 131080;BA.debugLine="fp.Initialize(\"FilePicker\")";
parent.mostCurrent._fp.Initialize(mostCurrent.activityBA,"FilePicker");
RDebugUtils.currentLine=131081;
 //BA.debugLineNum = 131081;BA.debugLine="fp.Properties = props";
parent.mostCurrent._fp.setProperties((com.github.angads25.filepicker.model.DialogProperties)(parent.mostCurrent._props.getObject()));
RDebugUtils.currentLine=131082;
 //BA.debugLineNum = 131082;BA.debugLine="Wait For FilePicker_Complete";
anywheresoftware.b4a.keywords.Common.WaitFor("filepicker_complete", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "activity_create"), null);
this.state = 19;
return;
case 19:
//C
this.state = 5;
;
RDebugUtils.currentLine=131083;
 //BA.debugLineNum = 131083;BA.debugLine="If item.Filename <> \"\" Then";
if (true) break;

case 5:
//if
this.state = 10;
if ((parent.mostCurrent._item.getFilename()).equals("") == false) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 10;
RDebugUtils.currentLine=131086;
 //BA.debugLineNum = 131086;BA.debugLine="Log(\"File name: \" & item.Filename)";
anywheresoftware.b4a.keywords.Common.LogImpl("4131086","File name: "+parent.mostCurrent._item.getFilename(),0);
 if (true) break;

case 9:
//C
this.state = 10;
RDebugUtils.currentLine=131088;
 //BA.debugLineNum = 131088;BA.debugLine="Msgbox(\"No Mission File Found - Exiting\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("No Mission File Found - Exiting"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
RDebugUtils.currentLine=131089;
 //BA.debugLineNum = 131089;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 if (true) break;

case 10:
//C
this.state = 11;
;
RDebugUtils.currentLine=131092;
 //BA.debugLineNum = 131092;BA.debugLine="Dim pattern As String";
_pattern = "";
RDebugUtils.currentLine=131093;
 //BA.debugLineNum = 131093;BA.debugLine="pattern = \"^(.*/)([^/]*)$\"";
_pattern = "^(.*/)([^/]*)$";
RDebugUtils.currentLine=131094;
 //BA.debugLineNum = 131094;BA.debugLine="Dim jsonText As String";
_jsontext = "";
RDebugUtils.currentLine=131095;
 //BA.debugLineNum = 131095;BA.debugLine="Dim filepath As String";
_filepath = "";
RDebugUtils.currentLine=131096;
 //BA.debugLineNum = 131096;BA.debugLine="Dim filename As String";
_filename = "";
RDebugUtils.currentLine=131097;
 //BA.debugLineNum = 131097;BA.debugLine="Dim Matcher1 As Matcher = Regex.Matcher(pattern,";
_matcher1 = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_matcher1 = anywheresoftware.b4a.keywords.Common.Regex.Matcher(_pattern,parent.mostCurrent._item.getFilename());
RDebugUtils.currentLine=131098;
 //BA.debugLineNum = 131098;BA.debugLine="Do While Matcher1.Find";
if (true) break;

case 11:
//do while
this.state = 18;
while (_matcher1.Find()) {
this.state = 13;
if (true) break;
}
if (true) break;

case 13:
//C
this.state = 14;
RDebugUtils.currentLine=131099;
 //BA.debugLineNum = 131099;BA.debugLine="Log(\"Found: \" & Matcher1.Match)";
anywheresoftware.b4a.keywords.Common.LogImpl("4131099","Found: "+_matcher1.getMatch(),0);
RDebugUtils.currentLine=131100;
 //BA.debugLineNum = 131100;BA.debugLine="For g = 1 To Matcher1.GroupCount";
if (true) break;

case 14:
//for
this.state = 17;
step25 = 1;
limit25 = _matcher1.getGroupCount();
_g = (int) (1) ;
this.state = 20;
if (true) break;

case 20:
//C
this.state = 17;
if ((step25 > 0 && _g <= limit25) || (step25 < 0 && _g >= limit25)) this.state = 16;
if (true) break;

case 21:
//C
this.state = 20;
_g = ((int)(0 + _g + step25)) ;
if (true) break;

case 16:
//C
this.state = 21;
RDebugUtils.currentLine=131101;
 //BA.debugLineNum = 131101;BA.debugLine="Log(\"Group #\" & g & \": \" & Matcher1.Group(g))";
anywheresoftware.b4a.keywords.Common.LogImpl("4131101","Group #"+BA.NumberToString(_g)+": "+_matcher1.Group(_g),0);
 if (true) break;
if (true) break;

case 17:
//C
this.state = 11;
;
RDebugUtils.currentLine=131103;
 //BA.debugLineNum = 131103;BA.debugLine="filepath = Matcher1.Group(1)";
_filepath = _matcher1.Group((int) (1));
RDebugUtils.currentLine=131104;
 //BA.debugLineNum = 131104;BA.debugLine="filename = Matcher1.Group(2)";
_filename = _matcher1.Group((int) (2));
 if (true) break;

case 18:
//C
this.state = -1;
;
RDebugUtils.currentLine=131107;
 //BA.debugLineNum = 131107;BA.debugLine="jsonText = File.ReadString(filepath, filename)";
_jsontext = anywheresoftware.b4a.keywords.Common.File.ReadString(_filepath,_filename);
RDebugUtils.currentLine=131108;
 //BA.debugLineNum = 131108;BA.debugLine="Log(jsonText) 'write the string to Log";
anywheresoftware.b4a.keywords.Common.LogImpl("4131108",_jsontext,0);
RDebugUtils.currentLine=131114;
 //BA.debugLineNum = 131114;BA.debugLine="Dim jsonMap As Map";
_jsonmap = new anywheresoftware.b4a.objects.collections.Map();
RDebugUtils.currentLine=131115;
 //BA.debugLineNum = 131115;BA.debugLine="jsonMap.Initialize";
_jsonmap.Initialize();
RDebugUtils.currentLine=131116;
 //BA.debugLineNum = 131116;BA.debugLine="jsonMap = ParseMSGMissionData(jsonText)";
_jsonmap = _parsemsgmissiondata(_jsontext);
RDebugUtils.currentLine=131117;
 //BA.debugLineNum = 131117;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _filepickerinitialize() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "filepickerinitialize", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "filepickerinitialize", null));}
anywheresoftware.b4a.objects.collections.List _filelist = null;
RDebugUtils.currentLine=720896;
 //BA.debugLineNum = 720896;BA.debugLine="Sub FilePickerInitialize";
RDebugUtils.currentLine=720897;
 //BA.debugLineNum = 720897;BA.debugLine="msgMissionJSONTest = $\"{\"comment\":\"MSG Mission, D";
mostCurrent._msgmissionjsontest = ("{\"comment\":\"MSG Mission, DJI Matrice 100\",\"version\": 1,\"msgMisInfo\": [{\"comment\":              \"MSG_CMD_MISSION: Used for mission settings\",\"id\":95,\"rawName\":\"MissionRaw\",\"friendlyName\":\"MSG Mission\",\"description\":\"Initial mission settings.\",\"specifiesCoordinate\":True,\"friendlyEdit\":True,\"category\":\"Mission\",\"param1\":{\"label\":\"Latitude\",\"default\":37.80378,\"decimalPlaces\":6},\"param2\":{\"label\":\"Longitude\",\"default\":-122.462276,\"decimalPlaces\":6}}]}");
RDebugUtils.currentLine=720898;
 //BA.debugLineNum = 720898;BA.debugLine="msgData = File.DirDefaultExternal & \"/MSGData\"";
mostCurrent._msgdata = anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal()+"/MSGData";
RDebugUtils.currentLine=720899;
 //BA.debugLineNum = 720899;BA.debugLine="fdIcon.Initialize(File.DirAssets, \"icons8-polygon";
mostCurrent._fdicon.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"icons8-polygon-50.png");
RDebugUtils.currentLine=720900;
 //BA.debugLineNum = 720900;BA.debugLine="Log(msgData)";
anywheresoftware.b4a.keywords.Common.LogImpl("4720900",mostCurrent._msgdata,0);
RDebugUtils.currentLine=720901;
 //BA.debugLineNum = 720901;BA.debugLine="If File.ExternalWritable = False Then";
if (anywheresoftware.b4a.keywords.Common.File.getExternalWritable()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=720902;
 //BA.debugLineNum = 720902;BA.debugLine="Msgbox(\"Cannot write on storage card.\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Cannot write on storage card."),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
RDebugUtils.currentLine=720903;
 //BA.debugLineNum = 720903;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=720905;
 //BA.debugLineNum = 720905;BA.debugLine="If File.IsDirectory(File.DirDefaultExternal,\"/MSG";
if (anywheresoftware.b4a.keywords.Common.File.IsDirectory(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"/MSGData")==anywheresoftware.b4a.keywords.Common.True) { 
RDebugUtils.currentLine=720906;
 //BA.debugLineNum = 720906;BA.debugLine="Dim fileList As List = File.ListFiles(msgData)";
_filelist = new anywheresoftware.b4a.objects.collections.List();
_filelist = anywheresoftware.b4a.keywords.Common.File.ListFiles(mostCurrent._msgdata);
 }else {
RDebugUtils.currentLine=720908;
 //BA.debugLineNum = 720908;BA.debugLine="File.MakeDir(File.DirDefaultExternal,\"/MSGData\")";
anywheresoftware.b4a.keywords.Common.File.MakeDir(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"/MSGData");
 };
RDebugUtils.currentLine=720910;
 //BA.debugLineNum = 720910;BA.debugLine="Return";
if (true) return "";
RDebugUtils.currentLine=720911;
 //BA.debugLineNum = 720911;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.collections.Map  _parsemsgmissiondata(String _jsonstring) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "parsemsgmissiondata", false))
	 {return ((anywheresoftware.b4a.objects.collections.Map) Debug.delegate(mostCurrent.activityBA, "parsemsgmissiondata", new Object[] {_jsonstring}));}
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.List _msgmisinfo = null;
anywheresoftware.b4a.objects.collections.Map _colmsgmisinfo = null;
String _altitude = "";
String _autoflightspeed = "";
String _description = "";
String _rawname = "";
anywheresoftware.b4a.objects.collections.Map _param1 = null;
double _default = 0;
int _decimalplaces = 0;
String _label = "";
anywheresoftware.b4a.objects.collections.Map _param2 = null;
String _friendlyedit = "";
String _maxflightspeed = "";
String _specifiescoordinate = "";
String _comment = "";
int _id = 0;
String _category = "";
String _finishaction = "";
String _friendlyname = "";
String _flightspeed = "";
int _version = 0;
RDebugUtils.currentLine=851968;
 //BA.debugLineNum = 851968;BA.debugLine="Sub ParseMSGMissionData(jsonstring As String) As M";
RDebugUtils.currentLine=851969;
 //BA.debugLineNum = 851969;BA.debugLine="Try";
try {RDebugUtils.currentLine=851971;
 //BA.debugLineNum = 851971;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
RDebugUtils.currentLine=851972;
 //BA.debugLineNum = 851972;BA.debugLine="parser.Initialize(jsonstring)";
_parser.Initialize(_jsonstring);
RDebugUtils.currentLine=851973;
 //BA.debugLineNum = 851973;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
RDebugUtils.currentLine=851974;
 //BA.debugLineNum = 851974;BA.debugLine="Dim msgMisInfo As List = root.Get(\"msgMisInfo\")";
_msgmisinfo = new anywheresoftware.b4a.objects.collections.List();
_msgmisinfo.setObject((java.util.List)(_root.Get((Object)("msgMisInfo"))));
RDebugUtils.currentLine=851975;
 //BA.debugLineNum = 851975;BA.debugLine="For Each colmsgMisInfo As Map In msgMisInfo";
_colmsgmisinfo = new anywheresoftware.b4a.objects.collections.Map();
{
final anywheresoftware.b4a.BA.IterableList group6 = _msgmisinfo;
final int groupLen6 = group6.getSize()
;int index6 = 0;
;
for (; index6 < groupLen6;index6++){
_colmsgmisinfo.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(group6.Get(index6)));
RDebugUtils.currentLine=851976;
 //BA.debugLineNum = 851976;BA.debugLine="Dim altitude As String = colmsgMisInfo.Get(\"alt";
_altitude = BA.ObjectToString(_colmsgmisinfo.Get((Object)("altitude")));
RDebugUtils.currentLine=851977;
 //BA.debugLineNum = 851977;BA.debugLine="Dim autoflightspeed As String = colmsgMisInfo.G";
_autoflightspeed = BA.ObjectToString(_colmsgmisinfo.Get((Object)("autoflightspeed")));
RDebugUtils.currentLine=851978;
 //BA.debugLineNum = 851978;BA.debugLine="Dim description As String = colmsgMisInfo.Get(\"";
_description = BA.ObjectToString(_colmsgmisinfo.Get((Object)("description")));
RDebugUtils.currentLine=851979;
 //BA.debugLineNum = 851979;BA.debugLine="Dim rawName As String = colmsgMisInfo.Get(\"rawN";
_rawname = BA.ObjectToString(_colmsgmisinfo.Get((Object)("rawName")));
RDebugUtils.currentLine=851980;
 //BA.debugLineNum = 851980;BA.debugLine="Dim param1 As Map = colmsgMisInfo.Get(\"param1\")";
_param1 = new anywheresoftware.b4a.objects.collections.Map();
_param1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_colmsgmisinfo.Get((Object)("param1"))));
RDebugUtils.currentLine=851981;
 //BA.debugLineNum = 851981;BA.debugLine="Dim default As Double = param1.Get(\"default\")";
_default = (double)(BA.ObjectToNumber(_param1.Get((Object)("default"))));
RDebugUtils.currentLine=851982;
 //BA.debugLineNum = 851982;BA.debugLine="Dim decimalPlaces As Int = param1.Get(\"decimalP";
_decimalplaces = (int)(BA.ObjectToNumber(_param1.Get((Object)("decimalPlaces"))));
RDebugUtils.currentLine=851983;
 //BA.debugLineNum = 851983;BA.debugLine="Dim label As String = param1.Get(\"label\")";
_label = BA.ObjectToString(_param1.Get((Object)("label")));
RDebugUtils.currentLine=851984;
 //BA.debugLineNum = 851984;BA.debugLine="Dim param2 As Map = colmsgMisInfo.Get(\"param2\")";
_param2 = new anywheresoftware.b4a.objects.collections.Map();
_param2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_colmsgmisinfo.Get((Object)("param2"))));
RDebugUtils.currentLine=851985;
 //BA.debugLineNum = 851985;BA.debugLine="Dim default As Double = param2.Get(\"default\")";
_default = (double)(BA.ObjectToNumber(_param2.Get((Object)("default"))));
RDebugUtils.currentLine=851986;
 //BA.debugLineNum = 851986;BA.debugLine="Dim decimalPlaces As Int = param2.Get(\"decimalP";
_decimalplaces = (int)(BA.ObjectToNumber(_param2.Get((Object)("decimalPlaces"))));
RDebugUtils.currentLine=851987;
 //BA.debugLineNum = 851987;BA.debugLine="Dim label As String = param2.Get(\"label\")";
_label = BA.ObjectToString(_param2.Get((Object)("label")));
RDebugUtils.currentLine=851988;
 //BA.debugLineNum = 851988;BA.debugLine="Dim friendlyEdit As String = colmsgMisInfo.Get(";
_friendlyedit = BA.ObjectToString(_colmsgmisinfo.Get((Object)("friendlyEdit")));
RDebugUtils.currentLine=851989;
 //BA.debugLineNum = 851989;BA.debugLine="Dim maxflightspeed As String = colmsgMisInfo.Ge";
_maxflightspeed = BA.ObjectToString(_colmsgmisinfo.Get((Object)("maxflightspeed")));
RDebugUtils.currentLine=851990;
 //BA.debugLineNum = 851990;BA.debugLine="Dim specifiesCoordinate As String = colmsgMisIn";
_specifiescoordinate = BA.ObjectToString(_colmsgmisinfo.Get((Object)("specifiesCoordinate")));
RDebugUtils.currentLine=851991;
 //BA.debugLineNum = 851991;BA.debugLine="Dim comment As String = colmsgMisInfo.Get(\"comm";
_comment = BA.ObjectToString(_colmsgmisinfo.Get((Object)("comment")));
RDebugUtils.currentLine=851992;
 //BA.debugLineNum = 851992;BA.debugLine="Dim id As Int = colmsgMisInfo.Get(\"id\")";
_id = (int)(BA.ObjectToNumber(_colmsgmisinfo.Get((Object)("id"))));
RDebugUtils.currentLine=851993;
 //BA.debugLineNum = 851993;BA.debugLine="Dim category As String = colmsgMisInfo.Get(\"cat";
_category = BA.ObjectToString(_colmsgmisinfo.Get((Object)("category")));
RDebugUtils.currentLine=851994;
 //BA.debugLineNum = 851994;BA.debugLine="Dim finishaction As String = colmsgMisInfo.Get(";
_finishaction = BA.ObjectToString(_colmsgmisinfo.Get((Object)("finishaction")));
RDebugUtils.currentLine=851995;
 //BA.debugLineNum = 851995;BA.debugLine="Dim friendlyName As String = colmsgMisInfo.Get(";
_friendlyname = BA.ObjectToString(_colmsgmisinfo.Get((Object)("friendlyName")));
RDebugUtils.currentLine=851996;
 //BA.debugLineNum = 851996;BA.debugLine="Dim flightspeed As String = colmsgMisInfo.Get(\"";
_flightspeed = BA.ObjectToString(_colmsgmisinfo.Get((Object)("flightspeed")));
 }
};
RDebugUtils.currentLine=851998;
 //BA.debugLineNum = 851998;BA.debugLine="Dim comment As String = root.Get(\"comment\")";
_comment = BA.ObjectToString(_root.Get((Object)("comment")));
RDebugUtils.currentLine=851999;
 //BA.debugLineNum = 851999;BA.debugLine="Dim version As Int = root.Get(\"version\")";
_version = (int)(BA.ObjectToNumber(_root.Get((Object)("version"))));
RDebugUtils.currentLine=852000;
 //BA.debugLineNum = 852000;BA.debugLine="Return root";
if (true) return _root;
 } 
       catch (Exception e33) {
			processBA.setLastException(e33);RDebugUtils.currentLine=852002;
 //BA.debugLineNum = 852002;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("4852002",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
RDebugUtils.currentLine=852003;
 //BA.debugLineNum = 852003;BA.debugLine="Msgbox(\"MSG Mission will now close\", \"JSON Parse";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("MSG Mission will now close"),BA.ObjectToCharSequence("JSON Parser Exception"),mostCurrent.activityBA);
RDebugUtils.currentLine=852004;
 //BA.debugLineNum = 852004;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
RDebugUtils.currentLine=852006;
 //BA.debugLineNum = 852006;BA.debugLine="End Sub";
return null;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=262146;
 //BA.debugLineNum = 262146;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=196608;
 //BA.debugLineNum = 196608;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=196609;
 //BA.debugLineNum = 196609;BA.debugLine="fp.show";
mostCurrent._fp.show();
RDebugUtils.currentLine=196610;
 //BA.debugLineNum = 196610;BA.debugLine="End Sub";
return "";
}
public static String  _filepicker_onselectedfilepaths(String[] _selected) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "filepicker_onselectedfilepaths", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "filepicker_onselectedfilepaths", new Object[] {_selected}));}
int _i = 0;
RDebugUtils.currentLine=786432;
 //BA.debugLineNum = 786432;BA.debugLine="Sub FilePicker_onSelectedFilePaths(selected() As S";
RDebugUtils.currentLine=786433;
 //BA.debugLineNum = 786433;BA.debugLine="Log($\"FilePicker_onSelectedFilePaths(${selected.L";
anywheresoftware.b4a.keywords.Common.LogImpl("4786433",("FilePicker_onSelectedFilePaths("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_selected.length))+")"),0);
RDebugUtils.currentLine=786434;
 //BA.debugLineNum = 786434;BA.debugLine="For i = 0 To selected.Length -1";
{
final int step2 = 1;
final int limit2 = (int) (_selected.length-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
RDebugUtils.currentLine=786435;
 //BA.debugLineNum = 786435;BA.debugLine="Log($\"${selected(i)}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("4786435",(""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_selected[_i]))+""),0);
 }
};
RDebugUtils.currentLine=786437;
 //BA.debugLineNum = 786437;BA.debugLine="If selected <> Null Then";
if (_selected!= null) { 
RDebugUtils.currentLine=786438;
 //BA.debugLineNum = 786438;BA.debugLine="item.Filename = selected(0)";
mostCurrent._item.setFilename(_selected[(int) (0)]);
 };
RDebugUtils.currentLine=786440;
 //BA.debugLineNum = 786440;BA.debugLine="fp.dismiss";
mostCurrent._fp.dismiss();
RDebugUtils.currentLine=786441;
 //BA.debugLineNum = 786441;BA.debugLine="CallSubDelayed(Me, \"FilePicker_Complete\")";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,main.getObject(),"FilePicker_Complete");
RDebugUtils.currentLine=786442;
 //BA.debugLineNum = 786442;BA.debugLine="End Sub";
return "";
}
public static String  _readmapexample() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "readmapexample", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "readmapexample", null));}
anywheresoftware.b4a.objects.collections.Map _map1 = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
int _i = 0;
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Sub ReadMapExample";
RDebugUtils.currentLine=983041;
 //BA.debugLineNum = 983041;BA.debugLine="Dim Map1 As Map";
_map1 = new anywheresoftware.b4a.objects.collections.Map();
RDebugUtils.currentLine=983043;
 //BA.debugLineNum = 983043;BA.debugLine="Map1 = File.ReadMap(File.DirRootExternal, \"Map.tx";
_map1 = anywheresoftware.b4a.keywords.Common.File.ReadMap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"Map.txt");
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
RDebugUtils.currentLine=983046;
 //BA.debugLineNum = 983046;BA.debugLine="sb.Initialize";
_sb.Initialize();
RDebugUtils.currentLine=983047;
 //BA.debugLineNum = 983047;BA.debugLine="sb.Append(\"The map entries are:\").Append(CRLF)";
_sb.Append("The map entries are:").Append(anywheresoftware.b4a.keywords.Common.CRLF);
RDebugUtils.currentLine=983048;
 //BA.debugLineNum = 983048;BA.debugLine="For i = 0 To Map1.Size - 1";
{
final int step6 = 1;
final int limit6 = (int) (_map1.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit6 ;_i = _i + step6 ) {
RDebugUtils.currentLine=983049;
 //BA.debugLineNum = 983049;BA.debugLine="sb.Append(\"Key = \").Append(Map1.GetKeyAt(i)).App";
_sb.Append("Key = ").Append(BA.ObjectToString(_map1.GetKeyAt(_i))).Append(", Value = ");
RDebugUtils.currentLine=983050;
 //BA.debugLineNum = 983050;BA.debugLine="sb.Append(Map1.GetValueAt(i)).Append(CRLF)";
_sb.Append(BA.ObjectToString(_map1.GetValueAt(_i))).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 }
};
RDebugUtils.currentLine=983052;
 //BA.debugLineNum = 983052;BA.debugLine="Msgbox(sb.ToString,\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_sb.ToString()),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
RDebugUtils.currentLine=983053;
 //BA.debugLineNum = 983053;BA.debugLine="End Sub";
return "";
}
public static String  _readtextreader() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "readtextreader", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "readtextreader", null));}
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
String _line = "";
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Sub ReadTextReader";
RDebugUtils.currentLine=1114113;
 //BA.debugLineNum = 1114113;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.DirRoo";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"Text.txt").getObject()));
RDebugUtils.currentLine=1114115;
 //BA.debugLineNum = 1114115;BA.debugLine="Dim line As String";
_line = "";
RDebugUtils.currentLine=1114116;
 //BA.debugLineNum = 1114116;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
RDebugUtils.currentLine=1114117;
 //BA.debugLineNum = 1114117;BA.debugLine="Do While line <> Null";
while (_line!= null) {
RDebugUtils.currentLine=1114118;
 //BA.debugLineNum = 1114118;BA.debugLine="Log(line) 'write the line to LogCat";
anywheresoftware.b4a.keywords.Common.LogImpl("41114118",_line,0);
RDebugUtils.currentLine=1114119;
 //BA.debugLineNum = 1114119;BA.debugLine="line = TextReader1.ReadLine";
_line = _textreader1.ReadLine();
 }
;
RDebugUtils.currentLine=1114121;
 //BA.debugLineNum = 1114121;BA.debugLine="TextReader1.Close";
_textreader1.Close();
RDebugUtils.currentLine=1114122;
 //BA.debugLineNum = 1114122;BA.debugLine="End Sub";
return "";
}
public static String  _writemapexample(anywheresoftware.b4a.objects.collections.Map _map,String _filename) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "writemapexample", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "writemapexample", new Object[] {_map,_filename}));}
int _i = 0;
RDebugUtils.currentLine=917504;
 //BA.debugLineNum = 917504;BA.debugLine="Sub WriteMapExample(map As Map, filename As String";
RDebugUtils.currentLine=917505;
 //BA.debugLineNum = 917505;BA.debugLine="map.Initialize";
_map.Initialize();
RDebugUtils.currentLine=917506;
 //BA.debugLineNum = 917506;BA.debugLine="For i = 1 To 10";
{
final int step2 = 1;
final int limit2 = (int) (10);
_i = (int) (1) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
RDebugUtils.currentLine=917507;
 //BA.debugLineNum = 917507;BA.debugLine="map.Put(\"Key\" & i, \"Value\" & i)";
_map.Put((Object)("Key"+BA.NumberToString(_i)),(Object)("Value"+BA.NumberToString(_i)));
 }
};
RDebugUtils.currentLine=917509;
 //BA.debugLineNum = 917509;BA.debugLine="File.WriteMap(File.DirRootExternal, filename, map";
anywheresoftware.b4a.keywords.Common.File.WriteMap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),_filename,_map);
RDebugUtils.currentLine=917510;
 //BA.debugLineNum = 917510;BA.debugLine="End Sub";
return "";
}
public static String  _writetextwriter() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "writetextwriter", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "writetextwriter", null));}
anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _textwriter1 = null;
int _i = 0;
RDebugUtils.currentLine=1048576;
 //BA.debugLineNum = 1048576;BA.debugLine="Sub WriteTextWriter";
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="Dim TextWriter1 As TextWriter";
_textwriter1 = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
RDebugUtils.currentLine=1048578;
 //BA.debugLineNum = 1048578;BA.debugLine="TextWriter1.Initialize(File.OpenOutput(File.DirRo";
_textwriter1.Initialize((java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"Text.txt",anywheresoftware.b4a.keywords.Common.False).getObject()));
RDebugUtils.currentLine=1048579;
 //BA.debugLineNum = 1048579;BA.debugLine="For i = 1 To 10";
{
final int step3 = 1;
final int limit3 = (int) (10);
_i = (int) (1) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
RDebugUtils.currentLine=1048580;
 //BA.debugLineNum = 1048580;BA.debugLine="TextWriter1.WriteLine(\"Line\" & i)";
_textwriter1.WriteLine("Line"+BA.NumberToString(_i));
 }
};
RDebugUtils.currentLine=1048582;
 //BA.debugLineNum = 1048582;BA.debugLine="TextWriter1.Close";
_textwriter1.Close();
RDebugUtils.currentLine=1048583;
 //BA.debugLineNum = 1048583;BA.debugLine="End Sub";
return "";
}
}