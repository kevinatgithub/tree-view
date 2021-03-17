package app.kevs.treeview.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.imu.flowerdelivery.network.ApiManager;
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler;
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler;
import com.imu.flowerdelivery.network.interfaces.TreeApi;
import com.imu.flowerdelivery.network.models.ResponseObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import app.kevs.treeview.Constants;
import app.kevs.treeview.NullApiRequestHandler;
import app.kevs.treeview.ProjectsActivity;
import app.kevs.treeview.network.models.NodeDto;
import app.kevs.treeview.network.models.Project;

public class NodesServices implements ObjectResponseHandler<Project>, ArrayResponseHandler<Project> {
    public static void PromptNodeName(Context ctx, PromptNodeNameCallback callback1, NodeZoomCallback zoomCallback){
        TreeApi api = ApiManager.Companion.getInstance(ctx);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Node Name");

        final LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(24,16,24,16);

        final EditText input = new EditText(ctx );
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(input);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add(Constants.Companion.getNODE_TYPE_OTHER());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_ADD_REFERENCE());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_MVC());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_SUB_PROJECT());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_IMPLEMENTATION());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_INTERFACE());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_CONTROLLER());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_MODEL());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_VIEW());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_CRUD());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_DEPENDENCY_MODEL());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_DEPENDENCY_REPOSITORY());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_DEPENDENCY_SERVICE());

        final Spinner projectsSpinner = new Spinner(ctx);
        ArrayList<String> projectNames = new ArrayList<>();
        for(Project project: ProjectsActivity.Companion.getProjects()){
            projectNames.add(project.getName());
        }

        ArrayAdapter<String> projectsSpinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, projectNames);
        projectsSpinner.setAdapter(projectsSpinnerAdapter);
        projectsSpinner.setVisibility(View.GONE);

        final Spinner refType = new Spinner(ctx);
        final String[] refTypes = new String[]{
                Constants.Companion.getREFERENCE_TYPE_BLANK(),
                Constants.Companion.getREFERENCE_TYPE_Strategy(),
        };
        ArrayAdapter<String> refTypeSpinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, refTypes);
        refType.setAdapter(refTypeSpinnerAdapter);
        refType.setVisibility(View.GONE);

        final Spinner nodeTypeSpinner = new Spinner(ctx);
        ArrayAdapter<String> nodeTypeSpinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        nodeTypeSpinner.setAdapter(nodeTypeSpinnerAdapter);
        nodeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (nodeTypeSpinner.getSelectedItem().toString().equals(Constants.Companion.getNODE_TYPE_ADD_REFERENCE())){
                    projectsSpinner.setVisibility(View.VISIBLE);
                    refType.setVisibility(View.VISIBLE);
                }else{
                    projectsSpinner.setVisibility(View.GONE);
                    refType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layout.addView(nodeTypeSpinner);
        layout.addView(projectsSpinner);
        layout.addView(refType);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nodeName = input.getText().toString();
                callback1.onNodeNameCaptured(nodeName, projectsSpinner.getSelectedItem().toString(), nodeTypeSpinner.getSelectedItem().toString(), refType.getSelectedItem().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        /*builder.setNeutralButton("Zoom In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zoomCallback.onZoom();
            }
        });*/

        builder.show();
    }

    public static void ConfirmNodeDeletion(Context ctx, NodeDeleteCallback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Delete Node");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onDelete();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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

    public interface NodeZoomCallback{
        void onZoom();
    }
}