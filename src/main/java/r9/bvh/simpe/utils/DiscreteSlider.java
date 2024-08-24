package r9.bvh.simpe.utils;
 

	import java.awt.BasicStroke;
	import java.awt.Color;
	import java.awt.Dimension;
	import java.awt.Graphics;
	import java.awt.Graphics2D;
	import java.awt.Stroke;
	import java.awt.event.MouseAdapter;
	import java.awt.event.MouseEvent;

	import javax.swing.JPanel;
 

	public    class DiscreteSlider extends JPanel{
		 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static int LABEL_WIDTH = 30;
		
		public static interface CALLBACK {
			void onValueChange(int value);
			Color getColor();
		}
		
		public Color color; 
		public Color lineColor = Color.gray;
		public final int width;
		public final int height;
		private int min;
		private int max;
		
		private CALLBACK callback;
		private int curValue;
	 
		private int ticks;
		
		float transRatio;
		private Color  bgColor = Color.WHITE;
		
		
		/**
		 * 
		 * @param width     UI width for slider  (not include value part)
		 * @param height
		 * @param color
		 * @param amin
		 * @param amax
		 * @param avalue
		 * @param aticks   num of ticks.  if <=0, use (amax - amin +1)
		 * @param transRatio    slider value *  transRatio =  real value.    if <code>transRatio</code> > 0, show real value.
		 * @param acallback
		 */
		public DiscreteSlider(  int width, int height,  Color color,   int amin, int amax, int avalue,
				int aticks,    CALLBACK acallback){
			this( width, height, color, amin, amax, avalue, aticks, 0, acallback);
		}
		/**
		 * 
		 * @param width     UI width for slider  (not include value part)
		 * @param height
		 * @param color
		 * @param amin
		 * @param amax
		 * @param avalue
		 * @param aticks   num of ticks.  if <=0, use (amax - amin +1) 
		 * @param acallback
		 */
		public DiscreteSlider(  int width, int height,  Color color,   int amin, int amax, int avalue,
				int aticks, float transRatio,  CALLBACK acallback){
			setLayout(null);
			this.width = width;
			this.height = height;
			this.color = color;
			this.min = amin;
			this.max = amax;
			this.callback = acallback;
			this.curValue = avalue;
			this.transRatio = transRatio;
			if( aticks <=0 ) {
				this.ticks = max - min + 1;
			} else {
				this.ticks = aticks;
			}
			this.addMouseListener(new MouseAdapter(){ 
				@Override
				public void mouseClicked(MouseEvent e) { 
					if( !DiscreteSlider.this.isEnabled())
						return;
					 double r = e.getX()* 1.0/getWidth();  
					 curValue = (int)( min + (max-min+1)*r);
					 curValue = Math.min(curValue, max);
					 if( callback != null)
					     callback.onValueChange(curValue);
					 DiscreteSlider.this.repaint();
				} 
			 });
		}
		private boolean showValue() {
			return this.transRatio > 0;
		}
		 
		public void setBgColor(Color color) {
			this.bgColor  = color;
			if( color == null )
				this.setOpaque(true);
		 	else
		 		this.setOpaque(false);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if( this.bgColor != null ) {
				g.setColor(bgColor);
			    g.fillRect(0,0, width, getHeight()); 
			}
			g.setColor(this.isEnabled() ? Color.BLACK : Color.GRAY);
			g.drawLine(0, getHeight()/2, width, getHeight()/2);
			 
			Stroke sk = ((Graphics2D)g).getStroke();
			((Graphics2D)g).setStroke(new BasicStroke(1));  
			
			double xgap =  width * 1.0 / ticks; 
			int h = getHeight() ;
			
			if( curValue != 0 ){
				Color curColor = callback == null ? null : callback.getColor();
				if(curColor == null)
					curColor = color;
				if( !this.isEnabled() ) {
					curColor = curColor.brighter();
				}
				g.setColor(curColor);
				int w =  width * (curValue - min+1) / (max-min+1);
			    g.fillRect( 0, 7, w, h-14); 
			}
			
			if(  isEnabled() ) {
				g.setColor(lineColor);	 
			} else {
				g.setColor(lineColor.brighter());	
			}
			 
			if( min == 0 )
				g.fillRect( 0, 7, (int) xgap, h-14);
			
			
			for(int i =0; i < ticks; i++){
				g.drawLine( (int)(xgap* i), 5,  (int)(xgap* i), h-10);
			} 
			((Graphics2D)g).setStroke(sk);  
			
			if( this.showValue() ) {
				float value = curValue * this.transRatio;
				String v = NumberHelp.trimFloat(value,1);
				g.drawString(v, width+ 5, 12);
			}
		}
		public int getValue(){
			return curValue;
		}
		public void setValue(int value){
			if( value < this.min || value > this.max ) return;
			this.curValue = value;
			repaint();
		}
		public void setCallback(CALLBACK callback){
			this.callback = callback;
		}
		
		public Color getColor() {
			return color;
		}


		public void setColor(Color color) {
			this.color = color;
		}


		public Color getLineColor() {
			return lineColor;
		}


		public void setLineColor(Color lineColor) {
			this.lineColor = lineColor;
		}

		public int getValueRange() {
			return max - min;
		}

		public int getMin() {
			return min;
		}


		public void setMin(int min) {
			this.min = min;
		}


		public int getMax() {
			return max;
		}


		public void setMax(int max) {
			this.max = max;
		}


		public int getWidth(){
			return width + (this.showValue() ? LABEL_WIDTH: 0);
		}
		public int getHeight(){
			return height;
		}
		
		public Dimension getPreferredSize(){
			return new Dimension(getWidth(), height);
		}
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}
		public Dimension getMaximumSize(){
			return getPreferredSize();
		}
	}