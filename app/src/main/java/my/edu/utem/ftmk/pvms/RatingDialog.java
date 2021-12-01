package my.edu.utem.ftmk.pvms;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hsalf.smileyrating.SmileyRating;

public class RatingDialog {
    public static int rating;

    public static void showRatingDialog(Context context, final DialogCallback dialogCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);

        AlertDialog alertDialog = builder.create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_rating_dialog, null);

        builder.setView(v);

        TextView btnDone = v.findViewById(R.id.btnDone);
        SmileyRating smileyRating = v.findViewById(R.id.smile_rating);

        smileyRating.setSmileySelectedListener(type -> {
            // You can compare it with rating Type
            switch (type) {
                case TERRIBLE:
                    break;
                case BAD:
                    break;
                case OKAY:
                    break;
                case GOOD:
                    break;
                case GREAT:
                    break;
            }
            rating = type.getRating();
        });

        btnDone.setOnClickListener(view -> {
            if (dialogCallback != null) {
                if (rating != 0) {
                    dialogCallback.callback(rating);
                    alertDialog.dismiss();
                } else
                    Toast.makeText(context, "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });
        rating = 0;
        builder.show();
    }

    public interface DialogCallback {
        void callback(int rating);
    }
}
