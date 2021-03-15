package app.kevs.treeview.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import app.kevs.treeview.Constants;
import app.kevs.treeview.StringNode;
import de.blox.treeview.TreeNode;

public class NodesServices {
    public static void PromptNodeName(Context ctx, PromptNodeNameCallback callback1, NodeZoomCallback zoomCallback){
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
        spinnerArray.add(Constants.Companion.getNODE_TYPE_MVC());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_SUB_PROJECT());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_IMPLEMENTATION());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_INTERFACE());
        spinnerArray.add(Constants.Companion.getNODE_TYPE_CONTROLLER());

        final Spinner spinner = new Spinner(ctx);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);

        layout.addView(spinner);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback1.onNodeNameCaptured(input.getText().toString(), spinner.getSelectedItem().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Zoom In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zoomCallback.onZoom();
            }
        });

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

    public interface PromptNodeNameCallback{
        void onNodeNameCaptured(String nodeName, String type);
    }

    public interface NodeDeleteCallback{
        void onDelete();
    }

    public interface NodeZoomCallback{
        void onZoom();
    }
}