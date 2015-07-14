package ca.uwaterloo.Lab4_205_05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
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
	 static PointF startPoint, endPoint;
	 static PointF userP;
	 PointF closestToS, closestToE;
	 PointF eye, jay; 
	 
	 PointF[] mapE2 = new PointF[3];
	 double sTocloseS;
	 double sTocloseE;
	 double sCloseToeClose;
	 String firstTurn;
	 String secondTurn;
	 public static TextView turnDir;
	 double angle;
	 double angle2;
	 
	 PointF originPt;
	 
     public void onClick(View v) { }
     	public void btnClick(View V){
     		steps += 1;
     		AccelerometerSensorEventListener.stepTrue = true;
     		Log.e("userPoint", String.valueOf(userP));
     	}
     	
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
        		
        		mapE2[0] = new PointF(3.8618f, 18.2654f);        		
        		mapE2[1] = new PointF(12.8469f, 18.2654f);
        		mapE2[2] = new PointF(20.6143f, 18.2654f);
        		//mapE2[3] = new PointF(21.3685f, 6.6246f);
        		
        		mv.setMap(map);
        		mv.addListener(new PositionListener(){
        			
					@Override
/*  -------   */  public void originChanged(MapView source, PointF loc) {
						startPoint = loc; 
						pathPoints.add(startPoint);
						userP = new PointF(source.getOriginPoint().x, source.getOriginPoint().y);	
					
					}
					
					@Override
					public void destinationChanged(MapView source, PointF dest) {
					endPoint = dest;
					mv.setUserPoint(userP);		
					
					//finding the closest point to startPoint
					if(map.calculateIntersections(startPoint, endPoint).size() != 0){
						
						for( int i = 0; i<3; i++){
							eye = mapE2[i];
							double deltaX = Math.abs(eye.x - startPoint.x);
							double deltaY = Math.abs(eye.y - startPoint.y);
							
							if(map.calculateIntersections(eye, startPoint).isEmpty() && 
										Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) <= 50f){
									closestToS = eye;
							}
						}
						pathPoints.add(closestToS);
						//if(closestToS == mapE2[3]){pathPoints.add(mapE2[2]);}
						
						//finding the closest point to endPoint
						if(map.calculateIntersections(closestToS, endPoint).size() != 0){
							for( int j = 0; j<3; j++){
								jay = mapE2[j];						
								double deltaX = Math.abs(jay.x - endPoint.x);
								double deltaY = Math.abs(jay.y - endPoint.y);
								
								if(map.calculateIntersections(jay, endPoint).isEmpty() &&
											Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) <= 1000f){
										closestToE = jay;
								}	
							}
							//if(closestToE == mapE2[3] && endPoint.y>closestToE.y){pathPoints.add(mapE2[2]);}
							pathPoints.add(closestToE);
						}
						
						
						double xOf_sTcS = -(startPoint.x - closestToS.x);
						double yOf_sTcS = -(startPoint.y - closestToS.y);
						double xOf_eTcE = endPoint.x - closestToE.x;
						double yOf_eTcE = endPoint.y - closestToE.y;
						double foo = Math.abs(closestToS.x - closestToE.x);
						double bar = Math.abs(closestToS.y - closestToE.y);
	/*distance btwn startpt and closestToS*/	sTocloseS = Math.sqrt(Math.pow(Math.abs(xOf_sTcS), 2) + Math.pow(Math.abs(yOf_sTcS), 2));
	/*distance btween endpt and closestToE*/	sTocloseE = Math.sqrt(Math.pow(Math.abs(xOf_eTcE), 2) + Math.pow(Math.abs(yOf_eTcE), 2)); 
						sCloseToeClose = Math.sqrt(Math.pow(foo, 2) + Math.pow(bar, 2));
						
							
						if((startPoint.x<endPoint.x && startPoint.y <= closestToS.y) || startPoint.x>endPoint.x && startPoint.y > closestToS.y){
							firstTurn = "LEFT";
							angle = 180-((Math.atan2((double)yOf_sTcS, (double)xOf_sTcS))*180/3.1415926);
						}
						else if((startPoint.x>endPoint.x && startPoint.y <= closestToS.y) || startPoint.x<endPoint.x && startPoint.y > closestToS.y){
							firstTurn = "RIGHT";
							angle = 180+((Math.atan2((double)yOf_sTcS, (double)xOf_sTcS))*180/3.1415926);
							if(angle>180) angle = angle - 180;
						}
						
						if((closestToE.y>endPoint.y && closestToS.x < closestToE.x) || closestToE.y<endPoint.y && closestToS.x > closestToE.x){
							secondTurn = "LEFT";
							angle2 = ((Math.atan2((double)yOf_eTcE, (double)xOf_eTcE))*180/3.1415926);
							if(angle2<0) angle2 = 180 + angle2;
						}
						else if((closestToE.y<endPoint.y && closestToS.x < closestToE.x) || closestToE.y>endPoint.y && closestToS.x > closestToE.x){
							secondTurn = "RIGHT";
							angle2 = ((Math.atan2((double)-yOf_eTcE, (double)xOf_eTcE))*180/3.1415926);
							if(angle2<0) angle2 = 180 + angle2;
						}
						
						if(closestToS != closestToE && map.calculateIntersections(closestToS, endPoint).size() != 0){
							turnDir.setTextSize(18);
							turnDir.setText("Go straight " + String.format("%.2f",sTocloseS) + "m \nturn " + firstTurn + " at " +
							String.format("%.2f", angle) + "' then go straight for "+ String.format("%.2f",sCloseToeClose) + "m \nturn "
									+ secondTurn + " at " + String.format("%.2f", angle2) + "' and walk for " + 
									String.format("%.2f",sTocloseE) + " m");
						}
						else{
							turnDir.setTextSize(18);
							turnDir.setText("Go straight " + String.format("%.2f",sTocloseS) + "m \nturn " + firstTurn + 
									" at " + String.format("%.2f", angle)+ "' \nwalk for " + String.format("%.2f",sTocloseE) + " m");
						}						
					}
					else{
						turnDir.setTextSize(18);
						turnDir.setText("Walk straight to reach your destination");
					}
					
					turnDir.setTextColor(Color.BLUE);
					pathPoints.add(endPoint);
					mv.setUserPath(pathPoints);
					
				}	        		
    		});
        }
    }

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if (id == R.id.item3) {
        	pathPoints.clear(); 
     		interceptPoint.clear();
     		startPoint = null;
     		endPoint = null;
        	Toast.makeText(this, "GPS path cleared", Toast.LENGTH_SHORT).show();
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
            turnDir = new TextView(rootView.getContext());
            
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

        	turnDir.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        	layout.addView(turnDir);
        	
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
