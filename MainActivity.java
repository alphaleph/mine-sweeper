package hu.ait.android.chau.minesweepergame;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hu.ait.android.chau.minesweepergame.Model.MinesweeperModel;
import hu.ait.android.chau.minesweepergame.View.GameView;


public class MainActivity extends ActionBarActivity {

    Button btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GameView gameView = (GameView) findViewById(R.id.gameView);
        final TextView tvMode = (TextView) findViewById(R.id.tvMode);
        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnToggle = (Button) findViewById(R.id.btnToggle);

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MinesweeperModel.getInstance().toggleMode();
                if (MinesweeperModel.getInstance().getMode() == MinesweeperModel.Mode.PLACEFLAG) {
                    tvMode.setText(getString(R.string.placeFlagMode));
                } else {
                    tvMode.setText(getString(R.string.tryFieldMode));
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.clearGameArea();
                tvMode.setText(getString(R.string.tryFieldMode));
            }
        });
    }

    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void flipToggleBtnEnable() {
        btnToggle.setEnabled(!btnToggle.isEnabled());
    }

}
