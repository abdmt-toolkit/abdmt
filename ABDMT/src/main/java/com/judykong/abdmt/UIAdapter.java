package com.judykong.abdmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.judykong.abdmt.AbdObserver.ObserverFlags;

import org.w3c.dom.Text;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UIAdapter {

    private class UIParams {
        public float textSize;
        public int width;
        public int height;
        public int visibility;
        public float brightness;
        public UIParams() {}
    }

    private class UIChange {
        public String type;
        public Map<Integer, UIParams> modifiedTargets;

        public UIChange(String type, Map<Integer, UIParams> modifiedTargets) {
            this.type = type;
            this.modifiedTargets = modifiedTargets;
        }
    }

    private static UIAdapter _uiadapter = null;
    private Window _window = null;

    public List<View> _targets;
    private Map<View, String> _taggedTargets;

    private Map<Integer, UIParams> _uiInitValues;
    private List<UIChange> _uiHistory;

    private UIAdapter() {
        _targets = new ArrayList<View>();
        _taggedTargets = new HashMap<View, String>();
        _uiInitValues = new HashMap<Integer, UIParams>();
        _uiHistory = new ArrayList<UIChange>();
    }

    // Not required for UIAdapt
    // registerUIComponents(root);
    // method; call only if developers need the targets to be adapted

    // Returns the UIAdapter singleton
    public static UIAdapter getInstance() {
        if (_uiadapter == null) {
            _uiadapter = new UIAdapter();
        }
        return _uiadapter;
    }

    // Register UI components
    public void registerWidgets(ViewGroup root) {
        registerAllChildren(root);
    }

    public void registerWidgets(ViewGroup root, boolean reset) {
        if (reset) {
            _targets.clear();
        }
        registerAllChildren(root);
    }

    private void registerAllChildren(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                registerAllChildren(g);
            } else {
                _targets.add(v);
            }
        }
    }

    // Resize widgets by pct
    public void resizeWidgets(double factor) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        if (factor <= 0) return;
        for (View v : _targets) {
            UIParams params = new UIParams();
            params.width = v.getWidth();
            params.height = v.getHeight();
            modification.put(v.getId(), params);
            updateViewWidthBy(v, factor);
            updateViewHeightBy(v, factor);
        }
        _uiHistory.add(new UIChange("resizeWidgets", modification));
    }

    public void resizeWidgets(double factor, String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        if (factor <= 0) return;
        for (View v : _targets) {
            if (_taggedTargets.get(v) == tag) {
                UIParams params = new UIParams();
                params.width = v.getWidth();
                params.height = v.getHeight();
                modification.put(v.getId(), params);
                updateViewWidthBy(v, factor);
                updateViewHeightBy(v, factor);
            }
        }
        _uiHistory.add(new UIChange("resizeWidgets", modification));
    }

    public void resizeWidgets(double factor, ViewGroup parent) {
        if (factor <= 0) return;
        Map<Integer, UIParams> modification = resizeWidgetsAllChildren(factor, parent, new HashMap<Integer, UIParams>());
        _uiHistory.add(new UIChange("resizeWidgets", modification));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void resizeWidgets(double factor, boolean basedOnInitialSize) {
        // Find initial resizeWidgets change
        if (factor <= 0) return;
        Map<Integer, UIParams> modifiedTargets = new HashMap<Integer, UIParams>();
        for (UIChange change : _uiHistory) {
            if (change.type == "resizeWidgets") {
                Map<Integer, UIParams> m = change.modifiedTargets;
                for (Integer id : m.keySet()) {
                    if (!modifiedTargets.containsKey(id)) {
                        modifiedTargets.put(id, m.get(id));
                    }
                }
            }
        }

        // If there's no prior record, directly apply changes
        if (!basedOnInitialSize || modifiedTargets.isEmpty()) {
            resizeWidgets(factor);
            return;
        }

        // Apply changes
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            UIParams params = new UIParams();
            params.width = v.getWidth();
            params.height = v.getHeight();
            modification.put(v.getId(), params);
            if (modification.containsKey(v.getId())) {
                updateViewWidthBy(v, (float)modifiedTargets.get(v.getId()).width * factor / (float)v.getMinimumWidth());
                updateViewHeightBy(v, (float)modifiedTargets.get(v.getId()).height * factor / (float)v.getMinimumHeight());
            } else {
                Log.i(TAG, "Not Contain" + v.getId() + ", " + v.getWidth() + ", " + v.getHeight());
                updateViewWidthBy(v, factor);
                updateViewHeightBy(v, factor);
            }
        }
        _uiHistory.add(new UIChange("resizeWidgets", modification));
    }

    private Map<Integer, UIParams> resizeWidgetsAllChildren(double factor, ViewGroup parent, Map<Integer, UIParams> modification) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                modification = resizeWidgetsAllChildren(factor, g, modification);
            } else {
                UIParams params = new UIParams();
                params.width = v.getWidth();
                params.height = v.getHeight();
                modification.put(v.getId(), params);
                updateViewWidthBy(v, factor);
                updateViewHeightBy(v, factor);
            }
        }
        return modification;
    }

    // Set minimum target sizes
    public void enforceMinSize(double bound) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            int width = v.getWidth();
            int height = v.getHeight();
            UIParams params = new UIParams();
            params.width = width;
            params.height = height;
            modification.put(v.getId(), params);
            float factor = 1;
            if (width * height > 0) {
                factor = Math.max((float) bound / width, (float) bound / height);
            }
            updateViewWidthBy(v, factor);
            updateViewHeightBy(v, factor);
        }
        _uiHistory.add(new UIChange("enforceMinSize", modification));
    }

    public void enforceMinSize(double bound, String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (_taggedTargets.get(v) == tag) {
                int width = v.getWidth();
                int height = v.getHeight();
                UIParams params = new UIParams();
                params.width = width;
                params.height = height;
                modification.put(v.getId(), params);
                float factor = 1;
                if (width * height > 0) {
                    factor = Math.max((float) bound / width, (float) bound / height);
                }
                updateViewWidthBy(v, factor);
                updateViewHeightBy(v, factor);
            }
        }
        _uiHistory.add(new UIChange("enforceMinSize", modification));
    }

    public void enforceMinSize(double bound, ViewGroup parent) {
        Map<Integer, UIParams> modification = enforceMinSizeAllChildren(bound, parent, new HashMap<Integer, UIParams>());
        _uiHistory.add(new UIChange("enforceMinSize", modification));
    }

    private Map<Integer, UIParams> enforceMinSizeAllChildren(double bound, ViewGroup parent, Map<Integer, UIParams> modification) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                modification = enforceMinSizeAllChildren(bound, g, modification);
            } else {
                int width = v.getWidth();
                int height = v.getHeight();
                UIParams params = new UIParams();
                params.width = width;
                params.height = height;
                modification.put(v.getId(), params);
                float factor = 1;
                if (width * height > 0) {
                    factor = Math.max((float) bound / width, (float) bound / height);
                }
                updateViewWidthBy(v, factor);
                updateViewHeightBy(v, factor);
            }
        }
        return modification;
    }

    // Resize fonts by pct
    public void resizeFonts(double factor) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        if (factor <= 0) return;
        for (View v : _targets) {
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                float fontSize = tv.getTextSize();
                UIParams params = new UIParams();
                params.textSize = fontSize;
                modification.put(v.getId(), params);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) factor); // trick here: for some reason the units don't match
            }
        }
        _uiHistory.add(new UIChange("resizeFonts", modification));
    }

    public void resizeFonts(double factor, String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        if (factor <= 0) return;
        for (View v : _targets) {
            if (v instanceof TextView) {
                if (_taggedTargets.get(v) == tag) {
                    TextView tv = (TextView) v;
                    float fontSize = tv.getTextSize();
                    UIParams params = new UIParams();
                    params.textSize = fontSize;
                    modification.put(v.getId(), params);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) factor); // trick here: for some reason the units don't match
                }
            }
        }
        _uiHistory.add(new UIChange("resizeFonts", modification));
    }

    public void resizeFonts(double factor, ViewGroup parent) {
        if (factor <= 0) return;
        Map<Integer, UIParams> modification = resizeFontsAllChildren(factor, parent, new HashMap<Integer, UIParams>());
        _uiHistory.add(new UIChange("resizeFonts", modification));
    }

    public void resizeFonts(double factor, boolean basedOnInitialSize) {
        // Find initial resizeFonts change
        if (factor <= 0) return;
        Map<Integer, UIParams> modifiedTargets = new HashMap<Integer, UIParams>();
        for (UIChange change : _uiHistory) {
            if (change.type == "resizeFonts") {
                Map<Integer, UIParams> m = change.modifiedTargets;
                for (Integer id : m.keySet()) {
                    if (!modifiedTargets.containsKey(id)) {
                        modifiedTargets.put(id, m.get(id));
                    }
                }
            }
        }

        // If there's no prior record, directly apply changes
        if (!basedOnInitialSize || modifiedTargets.isEmpty()) {
            resizeFonts(factor);
            return;
        }

        // Apply changes
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                UIParams params = new UIParams();
                params.textSize = tv.getTextSize();
                modification.put(v.getId(), params);
                float fontSize;
                if (modification.containsKey(v.getId())) {
                    fontSize = modifiedTargets.get(v.getId()).textSize;
                } else {
                    fontSize = tv.getTextSize();
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) factor); // trick here: for some reason the units don't match
            }
        }
        _uiHistory.add(new UIChange("resizeFonts", modification));
    }

    private Map<Integer, UIParams> resizeFontsAllChildren(double factor, ViewGroup parent, Map<Integer, UIParams> modification) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                modification = resizeFontsAllChildren(factor, g, modification);
            } else {
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    float fontSize = tv.getTextSize();
                    UIParams params = new UIParams();
                    params.textSize = fontSize;
                    modification.put(v.getId(), params);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) factor);
                }
            }
        }
        return modification;
    }

    // Set minimum font size
    public void enforceMinFontSize(int bound) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                float fontSize = tv.getTextSize();
                UIParams params = new UIParams();
                params.textSize = fontSize;
                modification.put(v.getId(), params);
                tv.setTextSize(Math.max(bound, fontSize));
            }
        }
        _uiHistory.add(new UIChange("enforceMinFontSize", modification));
    }

    public void enforceMinFontSize(int bound, String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (v instanceof TextView) {
                if (_taggedTargets.get(v) == tag) {
                    TextView tv = (TextView) v;
                    float fontSize = tv.getTextSize();
                    UIParams params = new UIParams();
                    params.textSize = fontSize;
                    modification.put(v.getId(), params);
                    tv.setTextSize(Math.max(bound, fontSize));
                }
            }
        }
        _uiHistory.add(new UIChange("enforceMinFontSize", modification));
    }

    public void enforceMinFontSize(int bound, ViewGroup parent) {
        Map<Integer, UIParams> modification = enforceMinFontSizeAllChildren(bound, parent, new HashMap<Integer, UIParams>());
        _uiHistory.add(new UIChange("enforceMinFontSize", modification));
    }

    private Map<Integer, UIParams> enforceMinFontSizeAllChildren(int bound, ViewGroup parent, Map<Integer, UIParams> modification) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                modification = enforceMinFontSizeAllChildren(bound, g, modification);
            } else {
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    float fontSize = tv.getTextSize();
                    UIParams params = new UIParams();
                    params.textSize = fontSize;
                    modification.put(v.getId(), params);
                    tv.setTextSize(Math.max(bound, fontSize));
                }
            }
        }
        return modification;
    }

    // Filter by tag / priority
    public void showOnlyWidgetsWithTag(String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (_taggedTargets.get(v) != tag) {
                UIParams params = new UIParams();
                params.visibility = v.getVisibility();
                modification.put(v.getId(), params);
                v.setVisibility(View.GONE);
            } else {
                UIParams params = new UIParams();
                params.visibility = v.getVisibility();
                modification.put(v.getId(), params);
                v.setVisibility(View.VISIBLE);
            }
        }
        _uiHistory.add(new UIChange("showOnlyWidgetsWithTag", modification));
    }

    // Filter by tag / priority
    public void showWidgetsWithTag(String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (_taggedTargets.get(v) == tag) {
                UIParams params = new UIParams();
                params.visibility = v.getVisibility();
                modification.put(v.getId(), params);
                v.setVisibility(View.VISIBLE);
            }
        }
        _uiHistory.add(new UIChange("showWidgetsWithTag", modification));
    }

    public void hideWidgetsWithTag(String tag) {
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        for (View v : _targets) {
            if (_taggedTargets.get(v) == tag) {
                UIParams params = new UIParams();
                params.visibility = v.getVisibility();
                modification.put(v.getId(), params);
                v.setVisibility(View.GONE);
            }
        }
        _uiHistory.add(new UIChange("hideAllWidgetsWithTag", modification));
    }

    // Set UI Component tags
    public void setTag(View v, String tag) {
        _taggedTargets.put(v, tag);
    }

    public void setTagAllChildren(View v, String tag) {
        _taggedTargets.put(v, tag);
        if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                setTagAllChildren(child, tag);
            }
        }
    }

    // Change screen brightness
    public void changeBrightness(ABDMTActivity activity, double factor) {
        if (factor < 0) {
            return;
        }
        _window = activity.getWindow();
        WindowManager.LayoutParams layout = _window.getAttributes();
        float currentBrightness = layout.screenBrightness;

        UIParams params = new UIParams();
        params.brightness = currentBrightness;
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        modification.put(0, params);
        layout.screenBrightness = (float) Math.max(currentBrightness * factor, 1);

        _uiHistory.add(new UIChange("changeBrightness", modification));
        _window.setAttributes(layout);
    }

    // Chagne brightness based on initial brightness value
    public void changeBrightness(ABDMTActivity activity, double factor, boolean basedOnInitialBrightness) {

        // Find initial brightness change
        if (factor <= 0) return;
        Map<Integer, UIParams> modifiedTargets = new HashMap<Integer, UIParams>();
        for (UIChange change : _uiHistory) {
            if (change.type == "changeBrightness") {
                Map<Integer, UIParams> m = change.modifiedTargets;
                for (Integer id : m.keySet()) {
                    if (!modifiedTargets.containsKey(id)) {
                        modifiedTargets.put(id, m.get(id));
                    }
                }
            }
        }

        // If there's no prior record, directly apply changes
        if (!basedOnInitialBrightness || modifiedTargets.isEmpty()) {
            changeBrightness(activity, factor);
            return;
        }

        _window = activity.getWindow();
        WindowManager.LayoutParams layout = _window.getAttributes();
        float currentBrightness = layout.screenBrightness;

        UIParams params = new UIParams();
        params.brightness = currentBrightness;
        Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
        modification.put(0, params);
        float initBrightness = modifiedTargets.get(0).brightness;
        if (initBrightness < 0 && factor == 1) { // Reverts back to preferred brightness
            layout.screenBrightness = initBrightness;
        } else {
            layout.screenBrightness = (float) Math.max(modifiedTargets.get(0).brightness * factor, 1);
        }

        _uiHistory.add(new UIChange("changeBrightness", modification));
        _window.setAttributes(layout);
    }

    // Revert UI
    public void revertUI() {
        if (_uiHistory.size() == 0) return;
        UIChange history = _uiHistory.get(_uiHistory.size() - 1);
        _uiHistory.remove(_uiHistory.size() - 1);
        String historyType = history.type;
        Map<Integer, UIParams> modifiedTargets = history.modifiedTargets;
        if (historyType == "resizeWidgets" || historyType == "enforceMinSize") {
            for (View view : _targets) {
                if (modifiedTargets.containsKey(view.getId())) {
                    UIParams params = modifiedTargets.get(view.getId());
                    view.setMinimumWidth(params.width);
                    view.setMinimumHeight(params.height);
                }
            }
        } else if (historyType == "resizeFonts" || historyType == "enforceMinFontSize") {
            for (View view : _targets) {
                if (modifiedTargets.containsKey(view.getId())) {
                    UIParams params = modifiedTargets.get(view.getId());
                    TextView tv = (TextView) view;
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.textSize);
                }
            }
        } else if (historyType == "showOnlyWidgetsWithTag" || historyType == "showWidgetsWithTag" || historyType == "hideWidgetsWithTag") {
            for (View view : _targets) {
                if (modifiedTargets.containsKey(view.getId())) {
                    UIParams params = modifiedTargets.get(view.getId());
                    view.setVisibility(params.visibility);
                }
            }
        } else if (historyType == "changeBrightness") {
            WindowManager.LayoutParams layout = _window.getAttributes();
            if (modifiedTargets.containsKey(0)) {
                UIParams params = modifiedTargets.get(0);
                layout.screenBrightness = params.brightness;
                _window.setAttributes(layout);
            }
        }
    }

    // Click button
    public void sendClick(Button button) {
        /*
        if (!(view instanceof Button))
            return;
        Button button = (Button) view;
        */
        button.performClick();
    }

    /* This needs further thinking on whether or not it's useful
    public void showAlertDialog(ABDMTActivity activity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    } */

    // Load UI resource
    public void loadUISource(ABDMTActivity activity, int layoutId) {
        activity.setContentView(layoutId);
    }

    // Apply transformations to UI components
    public void loadUITransformation(ABDMTActivity activity, int resourceId) {
        TransformationResponse response = getTransformationResponse(activity, resourceId);
        for (TransformationItem t : response.items) {
            switch (t.type) {
                case "Button":
                    applyButtonTransformations(activity, t.transformation);
                    break;
                case "TextView":
                    applyTextViewTransformations(activity, t.transformation);
                    break;
                default:
                    break;
            }
        }
    }

    /*******************************************/
    /************ Helper Functions *************/
    /*******************************************/

    TransformationResponse getTransformationResponse(ABDMTActivity activity, int resourceId) {
        // Load Json string
        InputStream is = activity.getResources().openRawResource(resourceId);
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String json_string = s.hasNext() ? s.next() : "";

        // Parse response
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json_string, TransformationResponse.class);
    }

    private void applyButtonTransformations(ABDMTActivity activity, List<Transformation> ts) {
        for (View v : _targets) {
            int id = v.getId();
            if (v instanceof Button) {
                Button bu = activity.findViewById(id);
                for (Transformation tr : ts) {
                    if (tr.transformationAttribute.contentEquals("Width")) {
                        updateViewWidthBy((View) bu, tr.transformationFactor);
                    }
                    if (tr.transformationAttribute.contentEquals("Height")) {
                        updateViewHeightBy((View) bu, tr.transformationFactor);
                    }
                }
            }
        }
    }

    private void applyTextViewTransformations(ABDMTActivity activity, List<Transformation> ts) {
        for (View v : _targets) {
            int id = v.getId();
            if (v instanceof TextView) {
                TextView text = activity.findViewById(id);
                for (Transformation tr : ts) {
                    if (tr.transformationAttribute.contentEquals("TextSize")) {
                        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (text.getTextSize() * tr.transformationFactor));
                    }
                }
            }
        }
    }

    public void updateViewWidthBy(View v, double factor) {
        v.setMinimumWidth((int) (v.getWidth() * factor));
    }

    public void updateViewHeightBy(View v, double factor) {
        v.setMinimumHeight((int) (v.getHeight() * factor));
    }
}