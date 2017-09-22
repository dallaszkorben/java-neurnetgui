package hu.akoel.neurnetgui.networkcanvas;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.sprite.SpriteCanvas;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.TranslateValue;

public class NetworkCanvas  extends SpriteCanvas{

	private static final long serialVersionUID = 2828047024539494605L;

	//NetworkCanvas
	//private static NetworkCanvas networkCanvas;	
	private static Color networkCanvasBackground = Color.black;
	private static TranslateValue networkCanvasPositionToMiddle = new TranslateValue( 0, 0 );	
	private static PossiblePixelPerUnits networkCanvasPossiblePixelPerUnits = new PossiblePixelPerUnits( new PixelPerUnitValue( 42, 42 ) ); //It is irrelevant as I use Scale

	//Grid
	private Grid networkCanvasGrid;	
	private Color networkCanvasGridColor = Color.green;
	private int networkCanvasGridWidth = 1;
	private DeltaValue networkCanvasGridDelta = new DeltaValue(1.0, 1.0);
	private Grid.PainterPosition networkCanvasGridPosition = Grid.PainterPosition.DEEPEST; 
	private Grid.Type networkCanvasGridType = Grid.Type.DOT;

	//Scale
	private Scale networkCanvasScale;
	private double networkCanvasPixelPerCm = 42.1;
	private Scale.UNIT networkCanvasUnit = Scale.UNIT.m;
	private double networkCanvasStartScale = 100;
	private double networkCanvasRate = 1.2;
	private double networkCanvasMinScale = 20; // < 1:20
	private double networkCanvasMaxScale = 220; // > 1:220
	
	public NetworkCanvas(){
		super( BorderFactory.createLoweredBevelBorder(), networkCanvasBackground, networkCanvasPossiblePixelPerUnits, networkCanvasPositionToMiddle );
		
		
    	networkCanvasGrid = new Grid( this, networkCanvasGridType, networkCanvasGridColor, networkCanvasGridWidth, networkCanvasGridPosition, networkCanvasGridDelta );
    	networkCanvasGrid.turnOn();
    	networkCanvasScale = new Scale( this, networkCanvasPixelPerCm, networkCanvasUnit, networkCanvasStartScale, networkCanvasRate, networkCanvasMinScale, networkCanvasMaxScale );
/*    	networkCanvasScale.addScaleChangeListener(new ScaleChangeListener() {
			public void getScale(Value scale) {
				DecimalFormat df = new DecimalFormat("#.00");
				if( networkCanvasScale.getScale().getX() < 1.0 ){
					System.err.println( "M=" + df.format(1/networkCanvasScale.getScale().getX() ) + ":1" );
				}else{
					System.err.println( "M=1:" + df.format(networkCanvasScale.getScale().getX() ) );
				}
			}
		});
*/  	
/*    	this.addPainterListenerToDeepest( new PainterListener() {
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				double x1 = 0.0;
				double y1 = 0.0;
				double x2 = 1.3;
				double y2 = 1.3;
				g2.setColor( Color.red );
				g2.setStroke( new BasicStroke(1));
				g2.drawRectangle(x1, y1, x2, y2);
				//g2.drawLine( x0, y0, x1, y0 );
				//g2.drawLine( x0, y0, x0, y1 );
			}
			
			public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {					
			}
		}, MCanvas.Level.ABOVE );
*/
	}
	public NetworkCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits,
			TranslateValue positionToMiddle ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle );
		
	}

}
