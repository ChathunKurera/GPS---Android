package ca.uwaterloo.Lab4_205_05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	 static LineGraphView graph;
	 public static int steps = 0;
	 static MapView  mv;
	 public static float[] gravity;
	 List<PointF> pathPoints = new ArrayList<PointF>();
	 List<InterceptPoint> interceptPoint = new ArrayList<InterceptPoint>(); 
	 static PointF startPoint;
	 static PointF endPoint;
	 static PointF userP;
	 
	 
     public void onClick(View v) { }
     	public void btnClick(View V){
     		steps += 1;
     		AccelerometerSensorEventListener.stepTrue = true;
     	}
     	
 	/*public void btnClick1(View v){
 		MagnetoSensorEventListener.eastSteps = 0;
     	}*/
     	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
    		getFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();  
    		
    		graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
    		graph.setVisibility(View.VISIBLE);
    		
    			        			
        		mv = new  MapView(getApplicationContext(), 1200, 800, 35, 35);
        		registerForContextMenu(mv);
        		final NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null),"E2-3344.svg");
        		
        		mv.setMap(map);
        		mv.addListener(new PositionListener(){

				@Override
				public void originChanged(MapView source, PointF loc) {
					//mv.setUserPoint(loc);
					userP = loc;
					startPoint = loc; //assign a start point to be referenced within our code
					pathPoints.add(loc);
					
				}

				@Override
				public void destinationChanged(MapView source, PointF dest) {
					endPoint = dest;
					
					interceptPoint = map.calculateIntersections(startPoint, endPoint);
					
					for(int g= 0; g < interceptPoint.size();g++){
						Log.e("points", String.valueOf(interceptPoint.get(g).getLine().end.x) + "," + String.valueOf(interceptPoint.get(g).getLine().end.y));
					}
					
					
					pathPoints.add(endPoint);
					mv.setUserPath(pathPoints);
				}
    			
    		});
        }
    }

	//the method updateUserPoint updates the user's current position
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		mv.onCreateContextMenu(menu,v,menuInfo);
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item){
		return super.onContextItemSelected(item)|| mv.onContextItemSelected(item);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.item1) {
        	steps = 0;
        	Toast.makeText(this, "Steps reset to zero", Toast.LENGTH_SHORT).show();
        }
        if( id == R.id.item2){
	        MagnetoSensorEventListener.eastSteps = 0;
	 		MagnetoSensorEventListener.northSteps = 0;
	 		MagnetoSensorEventListener.netDis = 0;
	 		MagnetoSensorEventListener.netDir = 0;
	 		Toast.makeText(this, "Direction Steps reset to zero", Toast.LENGTH_SHORT).show();
        }
		return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
    	private SeekBar valueC;
    	SensorEventListener a;
        public PlaceholderFragment(){        	
        }
                
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.layout_Main);
            layout.setOrientation(LinearLayout.VERTICAL);
        	TextView stepsEN = new TextView(rootView.getContext());
            TextView accelLabel = new TextView(rootView.getContext());
            TextView rotatLabel = new TextView(rootView.getContext());
            TextView magntLabel = new TextView(rootView.getContext());
            
            SensorManager sensorManager = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE);
            
            Sensor magneto = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        	SensorEventListener m = new MagnetoSensorEventListener(magntLabel, stepsEN);
        	sensorManager.registerListener(m, magneto, SensorManager.SENSOR_DELAY_GAME);
        	magntLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        	stepsEN.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        	layout.addView(magntLabel);
        	layout.addView(stepsEN);
         
        	Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            a = new AccelerometerSensorEventListener(accelLabel);
        	sensorManager.registerListener(a, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        	accelLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        	layout.addView(accelLabel);
        	
        	Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        	SensorEventListener r = new RotationalSensorEventListener(rotatLabel);
        	sensorManager.registerListener(r, rotation, SensorManager.SENSOR_DELAY_NORMAL);
        	layout.addView(rotatLabel);
        	
        	layout.addView(mv);
        	
        	//layout.addView(graph);
        	valueC = (SeekBar) rootView.findViewById(R.id.seekBar1);
            valueC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			
    			@Override
    			public void onStopTrackingTouch(SeekBar seekBar) {
    				// TODO Auto-generated method stub
    				
    			}
    			
    			@Override
    			public void onStartTrackingTouch(SeekBar seekBar) {
    				// TODO Auto-generated method stub
    				
    			}
    			
    			@Override
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromUser) {
    				 ((AccelerometerSensorEventListener) a).barChanged(progress+ 1);
    				
    			}
    		});
        	
        	return rootView; 
        }
    }
}
