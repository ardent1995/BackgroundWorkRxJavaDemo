package se.indpro.backgroundworkrxjavademo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button button;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<String> _logs;
    private LogAdapter _adapter;
    private CompositeDisposable _disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        recyclerView = findViewById(R.id.rv_logs);
        progressBar = findViewById(R.id.progressBar);
        _setupLogger();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked();
            }
        });
    }

    private void buttonClicked(){
//        Toast.makeText(this,"Button Clicked",Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.VISIBLE);
        _log("Button Clicked");
        DisposableObserver<Boolean> d = _getDisposableObserver();
        _getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d);
        _disposables.add(d);
    }

    private Observable<Boolean> _getObservable() {
        return Observable.just(false)
                .map(
                        new Function<Boolean, Boolean>() {
                            @Override
                            public Boolean apply(Boolean aBoolean) throws Exception {
                                MainActivity.this._log("Within Observable");
                                MainActivity.this._doSomeLongOperation_thatBlocksCurrentThread();
                                return aBoolean;
                            }
                        });
    }

    private void _doSomeLongOperation_thatBlocksCurrentThread() {
        _log("performing long operation");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.d(TAG,"Operation was interrupted");
        }
    }

    private DisposableObserver<Boolean> _getDisposableObserver(){
        return new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                _log(String.format("onNext with return value \"%b\"", aBoolean));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error in RxJava Demo concurrency");
                _log(String.format("Boo! Error %s", e.getMessage()));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onComplete() {
                _log("On complete");
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void _log(String logMsg) {

        if (_isCurrentlyOnMainThread()) {
            _logs.add(0, logMsg + " (main thread) ");
            _adapter.notifyDataSetChanged();
        } else {
            _logs.add(0, logMsg + " (NOT main thread) ");

            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper())
                    .post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    _adapter.notifyDataSetChanged();
                                }
                            });
        }
    }

    private boolean _isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private void _setupLogger() {
        _logs = new ArrayList<>();
        _adapter = new LogAdapter(_logs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(_adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _disposables.clear();
    }
}
