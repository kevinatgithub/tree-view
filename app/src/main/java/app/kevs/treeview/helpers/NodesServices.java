package app.kevs.treeview.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.core.content.res.ResourcesCompat;

import com.bakhtiyor.gradients.Gradients;
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler;
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler;
import com.imu.flowerdelivery.network.models.ResponseObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import app.kevs.treeview.Constants;
import app.kevs.treeview.ProjectsActivity;
import app.kevs.treeview.network.models.NodeDto;
import app.kevs.treeview.network.models.Project;

public class NodesServices implements ObjectResponseHandler<Project>, ArrayResponseHandler<Project> {
    public static void PromptNodeName(Context ctx, String nodeType, PromptNodeNameCallback callback1, NodeNeutralCallback callback2){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if (nodeType != null){
            setAddNodeDialogTitle(nodeType, builder);
        }else
            builder.setTitle("Node Name");


        final LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackground(Gradients.alchemistLab());
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(24,16,24,16);

        final EditText input = new EditText(ctx );
        input.setMinLines(1);
        input.setMaxLines(10);
        input.setBackground(ResourcesCompat.getDrawable(ctx.getResources(),android.R.drawable.screen_background_light_transparent,null));

        ArrayList<String> spinnerArray = getNodeTypeSpinnerArray();
        if (nodeType != null && nodeType.equals(Constants.NODE_TYPE_CONTAINER_INTERFACES)){
            spinnerArray = new ArrayList<>();
            spinnerArray.add(Constants.NODE_TYPE_INTERFACE);
        }

        final Spinner refTypeSpinner = new Spinner(ctx);
        final String[] refTypes = new String[]{
                Constants.REFERENCE_TYPE_BLANK,
                Constants.REFERENCE_TYPE_MVC,
                Constants.REFERENCE_TYPE_STRATEGY,
                Constants.REFERENCE_TYPE_CONTROLLER,
                Constants.REFERENCE_TYPE_MODEL,
        };
        ArrayAdapter<String> refTypeSpinnerAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, refTypes);
        refTypeSpinner.setAdapter(refTypeSpinnerAdapter);
        refTypeSpinner.setVisibility(View.GONE);

        final Spinner projectsSpinner = new Spinner(ctx);

        final Spinner nodeTypeSpinner = new Spinner(ctx);
        ArrayAdapter<String> nodeTypeSpinnerAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        nodeTypeSpinner.setAdapter(nodeTypeSpinnerAdapter);
        nodeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_OTHER)
                || nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_SUB_PROJECT)
                        || nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_CONTROLLER)
                        || nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_MODEL)
                        || nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_VIEW)){
                    projectsSpinner.setVisibility(View.GONE);
                    input.setVisibility(View.VISIBLE);
                    refTypeSpinner.setVisibility(View.GONE);
                }else if (nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_ADD_REFERENCE)){
                    projectsSpinner.setVisibility(View.VISIBLE);
                    input.setVisibility(View.GONE);
                    refTypeSpinner.setVisibility(View.GONE);
                }else if (nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_ADD_NEW_PROJECT_REFERENCE)){
                    projectsSpinner.setVisibility(View.GONE);
                    refTypeSpinner.setVisibility(View.VISIBLE);
                    input.setVisibility(View.VISIBLE);
                }else if (nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_MVC)
                || nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_INTERFACE)
                || nodeTypeSpinner.getSelectedItem().toString().equals(Constants.NODE_TYPE_LINK)){
                    projectsSpinner.setVisibility(View.GONE);
                    input.setVisibility(View.GONE);
                    refTypeSpinner.setVisibility(View.GONE);
                }else{
                    input.setVisibility(View.VISIBLE);
                    projectsSpinner.setVisibility(View.VISIBLE);
                    refTypeSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> projectNames = new ArrayList<>();
        for(Project project: ProjectsActivity.Companion.getProjects()){
            projectNames.add(project.getName());
        }

        ArrayAdapter<String> projectsSpinnerAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, projectNames);
        projectsSpinner.setAdapter(projectsSpinnerAdapter);
        projectsSpinner.setVisibility(View.GONE);



        layout.addView(nodeTypeSpinner);
        layout.addView(input);
        layout.addView(refTypeSpinner);
        layout.addView(projectsSpinner);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String nodeName = input.getText().toString();
            callback1.onNodeNameCaptured(nodeName, projectsSpinner.getSelectedItem().toString(), nodeTypeSpinner.getSelectedItem().toString(), refTypeSpinner.getSelectedItem().toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        if (nodeType != null){
            if (nodeType.equals(Constants.NODE_TYPE_INTERFACE)){
                builder.setNeutralButton("Add Implementation", ((dialog, which) -> callback2.onNeutralClick(input.getText().toString())));
            }else if (nodeType.equals(Constants.NODE_TYPE_CONTAINER_DEPENDENCIES)){
                builder.setNeutralButton("Add Dependency", (dialog, which) -> callback2.onNeutralClick(input.getText().toString()));
            }
        }

        builder.show();
    }

    @NotNull
    private static ArrayList<String> getNodeTypeSpinnerArray() {
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(Constants.NODE_TYPE_OTHER);
        spinnerArray.add(Constants.NODE_TYPE_ADD_NEW_PROJECT_REFERENCE);
        spinnerArray.add(Constants.NODE_TYPE_ADD_REFERENCE);
        spinnerArray.add(Constants.NODE_TYPE_MVC);
        spinnerArray.add(Constants.NODE_TYPE_SUB_PROJECT);
        spinnerArray.add(Constants.NODE_TYPE_INTERFACE);
        spinnerArray.add(Constants.NODE_TYPE_CONTROLLER);
        spinnerArray.add(Constants.NODE_TYPE_MODEL);
        spinnerArray.add(Constants.NODE_TYPE_CRUD);
        spinnerArray.add(Constants.NODE_TYPE_LINK);
        return spinnerArray;
    }

    private static void setAddNodeDialogTitle(String nodeType, AlertDialog.Builder builder) {
        if (nodeType.equals(Constants.NODE_TYPE_CONTAINER_METHODS))
            builder.setTitle("Methods");
        else if (nodeType.equals(Constants.NODE_TYPE_CONTAINER_ENDPOINTS))
            builder.setTitle("Endpoints");
        else
            builder.setTitle("Node Name");
    }

    public static void ConfirmNodeDeletion(Context ctx, NodeDeleteCallback callback, NodeUpdateCallback callback2){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Delete Node");
        builder.setPositiveButton("Yes", (dialog, which) -> callback.onDelete());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("Rename", (dialog, which) -> callback2.onUpdate(null));
        builder.show();
    }

    public static void ConfirmRefNodeDeletion(Context ctx, NodeDeleteCallback callback, NodeOnLinkCallback onLinkCallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Delete Node");
        builder.setPositiveButton("Yes", (dialog, which) -> callback.onDelete());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("Link", (dialog, which) -> onLinkCallback.onLink());
        builder.show();
    }

    public static void Rename(Context ctx, NodeUpdateCallback callback){
        final LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final EditText input = new EditText(ctx);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        linearLayout.addView(input);
        new AlertDialog.Builder(ctx)
                .setTitle("Rename Node")
                .setView(linearLayout)
                .setPositiveButton("Save", (dialog, which) -> callback.onUpdate(input.getText().toString()))
                .setNegativeButton("Cancel",(dialog, which) -> dialog.cancel())
        .show();
    }

    @Override
    public void onSuccess(@NotNull ResponseObject<Project> obj) {

    }

    @Override
    public void onError(@NotNull String error) {

    }

    @Override
    public void onSuccess(@NotNull Project[] collection) {
        ProjectsActivity.Companion.setProjects(new ArrayList<>(Arrays.asList(collection)));
    }

    public interface PromptNodeNameCallback{
        void onNodeNameCaptured(String nodeName, String externalProjectName, String type, String refType);
    }

    public interface NodeDeleteCallback{
        void onDelete();
    }
    public interface NodeUpdateCallback{
        void onUpdate(Object changes);
    }

    public interface NodeOnLinkCallback{
        void onLink();
    }

    public interface NodeNeutralCallback{
        void onNeutralClick(String name);
    }

    public interface DependencySelectedHandler{
        void onDependencySelected(NodeDto selected, String forType);
    }
}