package gnu.x11;

import gnu.x11.image.Image;


/** X drawable. */
public abstract class Drawable extends Resource {
  public int width, height;


  /** Predefined. */
  public Drawable (int id) {
    super (id);
  }


  /** Create. */
  public Drawable (Display display) {
    super (display);
  }


  /** Intern. */
  public Drawable (Display display, int id) {
    super (display, id);
  }


  // opcode 62 - copy area
  /**
   * Copies a specified rectangular area to another location.
   *
   * @param src the source drawable
   * @param gc the GC for the operation
   * @param src_x the source rectangle, x coordinate
   * @param src_y the source rectangle, y coordinate
   * @param width the width of the area to copy
   * @param height the height of the area to copy
   * @param dst_x the destination rectangle, x coordinate
   * @param dst_y the destination rectangle, y coordinate
   *
   * @see <a href="XCopyArea.html">XCopyArea</a>
   */
  public void copy_area (Drawable src, GC gc, int src_x, int src_y, 
                         int width, int height, int dst_x, int dst_y) {

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (62, 0, 7);
      o.write_int32 (src.id); // Src-drawable.
      o.write_int32 (id);     // Dst-drawable.
      o.write_int32 (gc.id);  // GC.
      o.write_int16 (src_x);
      o.write_int16 (src_y);
      o.write_int16 (dst_x);
      o.write_int16 (dst_y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.send ();
    }
  }


  // opcode 63 - copy plane
  /**
   * @see <a href="XCopyPlane.html">XCopyPlane</a>
   */
  public void copy_plane (Drawable src, GC gc, int src_x, int src_y, 
    int dst_x, int dst_y, int width, int height, int bit_plane) {

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (63, 0, 8);
      o.write_int32 (src.id);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      o.write_int16 (src_x);
      o.write_int16 (src_y);
      o.write_int16 (dst_x);
      o.write_int16 (dst_y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.write_int16 (bit_plane);
      o.send ();
    }
  }
    

  /**
   * Coordinate mode ORIGIN, specifies that points are always considered
   * relative to the origin.
   */
  public static final int ORIGIN = 0;

  /**
   * Coordinate mode PREVIOUS, specifies that points are considered relative
   * to the previous point (where the first point is usually considered
   * relative to the origin).
   */
  public static final int PREVIOUS = 1;

  // opcode 64 - poly point
  /**
   * Draws multiple points.
   *
   * @param gc the GC to use
   * @param xpoints the points' x coordinates
   * @param ypoints the points' y coodinates
   * @param npoints the number of points
   * @param coordinate_mode valid: {@link #ORIGIN}, {@link #PREVIOUS}
   * 
   * @see <a href="XDrawPoints.html">XDrawPoints</a>
   */
  public void poly_point (GC gc, int[] xpoints, int[] ypoints, int npoints,
                          int coordinate_mode) {

    // FIXME: Handle aggregation.
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (64, coordinate_mode, 3 + npoints);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < npoints; i++) {
        o.write_int16 (xpoints[i]);
        o.write_int16 (ypoints[i]);
      }
      o.send ();
    }
  }

  /**
   * Draws multiple points.
   *
   * @param gc the GC to use
   * @param xpoints the points' x coordinates
   * @param ypoints the points' y coodinates
   * @param npoints the number of points
   * @param coordinate_mode valid: {@link #ORIGIN}, {@link #PREVIOUS}
   * 
   * @see <a href="XDrawPoints.html">XDrawPoints</a>
   */
  public void poly_point (GC gc, Point[] points, int coordinate_mode) {

    // FIXME: Handle aggregation.
    int npoints = points.length;
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (64, coordinate_mode, 3 + points.length);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < npoints; i++) {
        o.write_int16 (points[i].x);
        o.write_int16 (points[i].y);
      }
      o.send ();
    }
  }

  // opcode 65 - poly line
  /**
   * Draws multiple lines that connect the specified points.
   *
   * @param gc the GC to use
   * @param xpoints the points' x coordinates
   * @param ypoints the points' y coodinates
   * @param npoints the number of points
   * @param coordinate_mode valid: {@link #ORIGIN}, {@link #PREVIOUS}
   * 
   * @see <a href="XDrawLines.html">XDrawLines</a>
   */
  public void poly_line (GC gc, int[] xpoints, int[] ypoints, int npoints,
                         int coordinate_mode) {

    // FIXME: Handle aggregation.
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (65, coordinate_mode, 3 + npoints);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < npoints; i++) {
        o.write_int16 (xpoints[i]);
        o.write_int16 (ypoints[i]);
      }
      o.send ();
    }
  }

  public void poly_line (GC gc, int[] xpoints, int[] ypoints, int npoints,
                         int coordinate_mode, boolean close) {

    // FIXME: Handle aggregation.
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (65, coordinate_mode, 3 + npoints + (close ? 1 : 0));
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < npoints; i++) {
        o.write_int16 (xpoints[i]);
        o.write_int16 (ypoints[i]);
      }
      if (close) {
        o.write_int16 (xpoints [0]);
        o.write_int16 (ypoints [0]);
      }
      o.send ();
    }
  }

  /**
   * Draws multiple lines which connect the specified points.
   *
   * @param gc the GC to use
   * @param points the points that make up the lines
   * @param coordinate_mode valid: {@link #ORIGIN}, {@link #PREVIOUS}
   * 
   * @see <a href="XDrawLines.html">XDrawLines</a>
   */
  public void poly_line (GC gc, Point[] points, int coordinate_mode) {

    // FIXME: Handle aggregation.
    int npoints = points.length;
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (65, coordinate_mode, 3 + points.length);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < npoints; i++) {
        o.write_int16 (points[i].x);
        o.write_int16 (points[i].y);
      }
      o.send ();
    }
  }

  // opcode 66 - poly segment
  /**
   * Draws multiple line segments
   *
   * @param gc the GC to use
   * @param segments the line segments to draw
   *
   * @see <a href="XDrawSegments.html">XDrawSegments</a>
   */
  public void poly_segment (GC gc, Segment [] segments) {

    // FIXME: Handle aggregation.

    int nsegs = segments.length;
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (66, 0, 3 + 2 * nsegs);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < nsegs; i++) {
        Segment seg = segments[i];
        o.write_int16 (seg.x1);
        o.write_int16 (seg.y1);
        o.write_int16 (seg.x2);
        o.write_int16 (seg.y2);
      }
      o.send ();
    }
  }

  // opcode 67 - poly rectangle
  /**
   * Draws the outline of multiple rectangles.
   *
   * @param gc the GC to use
   * @param rectangles the rectangles to draw
   *
   * @see <a href="XDrawRectangles.html">XDrawRectangles</a>
   * @see <a href="XFillRectangles.html">XFillRectangles</a>
   * @see Request.Aggregate aggregation
   */
  public void poly_rectangle (GC gc, Rectangle [] rectangles) {

    // FIXME: Handle aggregation.

    int nrects = rectangles.length;
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (67, 0, 3 + 2 * nrects);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      for (int i = 0; i < nrects; i++) {
        Rectangle rect = rectangles[i];
        o.write_int16 (rect.x);
        o.write_int16 (rect.y);
        o.write_int16 (rect.width);
        o.write_int16 (rect.height);
      }
      o.send ();
    }
  }

  // opcode 68 - poly arc
  /**
   * Draws the outline of multiple arcs.
  ����   0 �  gnu/x11/Drawable  gnu/x11/Resource width I height ORIGIN ConstantValue     PREVIOUS    COMPLEX 	NONCONVEX CONVEX    CURSOR TILE STIPPLE <init> (I)V Code
     LineNumberTable LocalVariableTable this Lgnu/x11/Drawable; id (Lgnu/x11/Display;)V
      display Lgnu/x11/Display; (Lgnu/x11/Display;I)V
  %  # 	copy_area '(Lgnu/x11/Drawable;Lgnu/x11/GC;IIIIII)V	  ) ! "	 + - , gnu/x11/Display . / 
connection Lgnu/x11/Connection;	 1 3 2 gnu/x11/Connection 4 5 out Lgnu/x11/RequestOutputStream;
 7 9 8 gnu/x11/RequestOutputStream : ; begin_request (III)V	  =  
 7 ? @  write_int32	 B = C 
gnu/x11/GC
 7 E F  write_int16
 7 H I J send ()V src gc Lgnu/x11/GC; src_x src_y dst_x dst_y o 
copy_plane ((Lgnu/x11/Drawable;Lgnu/x11/GC;IIIIIII)V 	bit_plane 
poly_point (Lgnu/x11/GC;[I[III)V xpoints [I ypoints npoints coordinate_mode i  (Lgnu/x11/GC;[Lgnu/x11/Point;I)V	 ` b a gnu/x11/Point c  x	 ` e f  y points [Lgnu/x11/Point; 	poly_line k java/lang/Error m dUnresolved compilation problem: 
	int is not a valid type's argument for the synchronized statement

 j o  p (Ljava/lang/String;)V (Lgnu/x11/GC;[I[IIIZ)V s �Unresolved compilation problems: 
	The method begin_request(int, int, int) is undefined for the type Connection
	The method end_request() is undefined for the type Connection
 close Z poly_segment !(Lgnu/x11/GC;[Lgnu/x11/Segment;)V segments [Lgnu/x11/Segment; poly_rectangle #(Lgnu/x11/GC;[Lgnu/x11/Rectangle;)V 
rectangles [Lgnu/x11/Rectangle; poly_arc (Lgnu/x11/GC;[Lgnu/x11/Arc;)V arcs [Lgnu/x11/Arc; 	fill_poly !(Lgnu/x11/GC;[Lgnu/x11/Point;II)V shape (Lgnu/x11/GC;[I[IIII)V poly_fill_rectangle poly_fill_arc put_small_image ((Lgnu/x11/GC;Lgnu/x11/image/Image;IIII)V image Lgnu/x11/image/Image; y1 y2 (IIIIII)Lgnu/x11/Data; � �Unresolved compilation problems: 
	The method begin_request(int, int, int) is undefined for the type Connection
	The method end_request() is undefined for the type Connection
	The method read_reply(null) is undefined for the type Display
 
plane_mask format 	poly_text  (Lgnu/x11/GC;II[Lgnu/x11/Text;)V texts [Lgnu/x11/Text; poly_text16 
image_text #(Lgnu/x11/GC;IILjava/lang/String;)V s Ljava/lang/String; image_text16 	best_size (III)Lgnu/x11/Size; �	Unresolved compilation problems: 
	The method begin_request(int, int, int) is undefined for the type Connection
	The method end_request() is undefined for the type Connection
	The method read_reply(int) in the type Connection is not applicable for the arguments ()
 klass arc (Lgnu/x11/GC;IIIIII)V angle1 angle2 fill_arc line (Lgnu/x11/GC;IIII)V x1 x2 point (Lgnu/x11/GC;II)V 	put_image &(Lgnu/x11/GC;Lgnu/x11/image/Image;II)V	 + � �  extended_maximum_request_length	 � � � gnu/x11/image/Image �  line_byte_count	 � �  
 � � � java/lang/Math � � min (II)I
  � � � max_data_byte request_height rem request_count 	rectangle fill_rectangle text 2(Lgnu/x11/GC;IILjava/lang/String;ILgnu/x11/Font;)V � gnu/x11/Text
 � �  � $(Ljava/lang/String;ILgnu/x11/Font;)V
  � � � delta font Lgnu/x11/Font; length ([Lgnu/x11/Text;I)I
 � � � � (I)I bit n 
SourceFile Drawable.java!     
               	    
     	         	    
     	         	         	    
     	         	     "        >     *� �       
                                >     *+� �       
                      ! "    #     I     *+� $�       
                       ! "         & '    <     p*� (� *� 0:		Y:
�	>� 6	+� <� >	*� <� >	,� A� >	� D	� D	� D	� D	� D	� D	� G
ç 
ÿ�   h k   k n k       >    /  0  1  2 % 3 . 4 7 5 = 6 D 7 K 8 R 9 Y : ` ; e 0 o =    f 
   p       p K     p L M    p N     p O     p      p      p P     p Q    d R 5 	  S T    Q     w*� (� *� 0:

Y:�
?� 6
+� <� >
*� <� >
,� A� >
� D
� D
� D
� D
� D
� D
	� D
� Gç ÿ�   o r   r u r       B    G  H  I  J % K . L 7 M = N D O K P R Q Y R ` S g T l H v V    p    w       w K     w L M    w N     w O     w P     w Q     w      w      w U  	  k R 5 
  V W      	   c*� (� *� 0:Y:�@`� 6*� <� >+� A� >6� ,.� D-.� D����� Gç ÿ�   [ ^   ^ a ^       2    v  w  x  y ( z 1 { 7 | @ } I { S  X w b �    R    c       c L M    c X Y    c Z Y    c [     c \    W R 5  4  ]    V ^         l,�6*� (� *� 0:Y:�@,�`� 6*� <� >+� A� >6� ,2� _� D,2� d� D����� Gç ÿ�   d g   g j g       6    �  �  �  � " � + � 4 � : � F � R � \ � a � k �    H    l       l L M    l g h    l \    h [    \ R 5  7 % ]    i W     f     
� jYl� n�           �    >    
       
 L M    
 X Y    
 Z Y    
 [     
 \    i q     p     
� jYr� n�           �    H    
       
 L M    
 X Y    
 Z Y    
 [     
 \     
 t u   i ^     R     
� jYr� n�           �    *    
       
 L M    
 g h    
 \    v w     H     
� jYr� n�           �         
       
 L M    
 x y   z {     H     
� jYr� n�                   
       
 L M    
 | }   ~      H     
� jYr� n�          +         
       
 L M    
 � �   � �     \     
� jYr� n�          \    4    
       
 L M    
 g h    
 �     
 \    � �     p     
� jYr� n�          |    H    
       
 L M    
 X Y    
 Z Y    
 [     
 �     
 \    � {     H     
� jYr� n�          �         
       
 L M    
 | }   �      H     
� jYr� n�          �         
       
 L M    
 � �   � �     p     
� jYr� n�          �    H    
       
 L M    
 � �    
 �     
 �     
 c     
 f    � �     p     
� jY�� n�          �    H    
       
 c     
 f     
      
      
 �     
 �    � �     \     
� jYr� n�          �    4    
       
 L M    
 c     
 f     
 � �   � �     \     
� jYr� n�              4    
       
 L M    
 c     
 f     
 � �   � �     \     
� jYr� n�          :    4    
       
 L M    
 c     
 f     
 � �   � �     \     
� jYr� n�          O    4    
       
 L M    
 c     
 f     
 � �   � �     R     
� jY�� n�          j    *    
       
 �     
      
     � �     z     
� jYr� n�          �    R    
       
 L M    
 c     
 f     
      
      
 �     
 �    � �     z     
� jYr� n�          �    R    
       
 L M    
 c     
 f     
      
      
 �     
 �    � �     f     
� jYr� n�          �    >    
       
 L M    
 �     
 �     
 �     
 �    � �     R     
� jYr� n�          �    *    
       
 L M    
 c     
 f    � �     	 
   g*� (� �hd6,� �l6,� �p6,� �l� � `66	� (*+,	h,� �	`h� �	h`� ��		��ױ       * 
  � � �  � 4� :� B� Y� \� f�    f 
   g       g L M    g � �    g c     g f    Y �    P �     G �   4 3 �   7 / ]  	  � �     f     
� jYr� n�          �    >    
       
 L M    
 c     
 f     
      
     � �     f     
� jYr� n�              >    
       
 L M    
 c     
 f     
      
     � �     �     *+� �Y� �Y� �S� α       
   " #    H            L M     c      f      � �     �      � �   � �     p     *+� �Y� �Y� �S� α       
   * +    4            L M     c      f      � �   � �     y     >6� +2� �`>�+�����          / 0 2    4    .begin_request (77, n, 4 + (2 * n + p) / 4);

    o.write_int32 (id);
    o.write_int32 (gc.id);
    o.write_int16 (x);
    o.write_int16 (y);
    o.write_string16 (s);

    o.write_pad (2 * n); // Pad.
    display.connection.end_request ();
  }


  public static final int CURSOR = 0;
  public static final int TILE = 1;
  public static final int STIPPLE = 2;

  // opcode 97 - query best size
  /**
   * @param klass valid:
   * {@link #CURSOR},
   * {@link #TILE},
   * {@link #STIPPLE}
   * 
   * @see <a href="XQueryBestSize.html">XQueryBestSize</a>
   */
  public Size best_size (int klass, int width, int height) {
    RequestOutputStream o = display.out;
    int w, h;
    synchronized (o) {
      o.begin_request (97, klass, 3);
      o.write_int32 (id);
      o.write_int16 (width);
      o.write_int16 (height);
      o.send ();

      ResponseInputStream i = display.in;
      synchronized (i) {
        i.read_reply(o);
        i.skip (8);
        w = i.read_int16 ();
        h = i.read_int16 ();
        i.skip (20);
      }
    }
    return new Size (w, h);
  }

  /**
   * Draws the outline of a single arc.
   *
   * @param gc the GC to use
   * @param x the bounding rectangle, x coordinate
   * @param y the bounding rectangle, y coordinate
   * @param w the bounding rectangle, width
   * @param h the bounding rectangle, height
   * @param angle1 the start angle, from 3 o'clock ccw, in degrees
   * @param angle2 the span angle, from angle1 ccw, in degrees
   *
   * @see #poly_arc(GC, Arc[])
   */
  public void arc (GC gc, int x, int y, int width, int height, 
                   int angle1, int angle2) {

    // FIXME: Handle aggregation.

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (68, 0, 6);
      o.write_int32 (id);
      o.write_int32 (gc.id);

      o.write_int16 (x);
      o.write_int16 (y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.write_int16 (angle1);
      o.write_int16 (angle2);
      o.send ();
    }
  }

  /**
   * Fills a single arc.
   *
   * @param gc the GC to use
   * @param x the bounding rectangle, x coordinate
   * @param y the bounding rectangle, y coordinate
   * @param w the bounding rectangle, width
   * @param h the bounding rectangle, height
   * @param angle1 the start angle, from 3 o'clock ccw, in degrees
   * @param angle2 the span angle, from angle1 ccw, in degrees
   *
   * @see #poly_arc(GC, Arc[])
   */
  public void fill_arc (GC gc, int x, int y, int width, int height, 
                        int angle1, int angle2) {

    // FIXME: Handle aggregation.

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (71, 0, 6);
      o.write_int32 (id);
      o.write_int32 (gc.id);

      o.write_int16 (x);
      o.write_int16 (y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.write_int16 (angle1);
      o.write_int16 (angle2);
      o.send ();
    }
  }

  /** 
   * Draws a single line.
   *
   * @param gc the GC to use
   * @param x1 the start point, x coordinate
   * @param y1 the start point, y coordinate
   * @param x2 the end point, x coordinate
   * @param y2 the end point, y coordinate
   */
  public void line (GC gc, int x1, int y1, int x2, int y2) {

    // FIXME: Handle aggregation.
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (65, ORIGIN, 5);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      o.write_int16 (x1);
      o.write_int16 (y1);
      o.write_int16 (x2);
      o.write_int16 (y2);
      o.send ();
    }
  }

  /**
   * Draws a single point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void point (GC gc, int x, int y) {
    // FIXME: Handle aggregation.
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (64, ORIGIN, 4);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      o.write_int16 (x);
      o.write_int16 (y);
      o.send ();
    }
  }

  public void put_image (GC gc, Image image, int x, int y) {
    // FIXME: Implement.
  }

  /**
   * Draws a single rectangle.
   *
   * @param gc the graphic context
   * @param x the upper left corner, x coordinate
   * @param y the upper left corner, y coordinate
   * @param width the width
   * @param height the height
   *
   * @see #poly_rectangle(GC, Rectangle[])
   */      
  public void rectangle (GC gc, int x, int y, int width, int height) {

    // FIXME: Handle aggregation.

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (67, 0, 5);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      o.write_int16 (x);
      o.write_int16 (y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.send ();
    }
  }

  /**
   * Fills a single rectangle.
   *
   * @param gc the graphic context
   * @param x the upper left corner, x coordinate
   * @param y the upper left corner, y coordinate
   * @param width the width
   * @param height the height
   *
   * @see #poly_fill_rectangle(GC, Rectangle[])
   */      
  public void fill_rectangle (GC gc, int x, int y, int width, int height) {

    // FIXME: Handle aggregation.

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (70, 0, 5);
      o.write_int32 (id);
      o.write_int32 (gc.id);
      o.write_int16 (x);
      o.write_int16 (y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.send ();
    }
  }

  public void poly_text8 (GC gc, int x, int y, Text[] texts) {

    // FIXME: Implement.
  }

  public void poly_text16 (GC gc, int x, int y, Text[] texts) {

    // FIXME: Implement.
  }

  /**
   * @see #poly_text(GC, int, int, Text[])
   */
  public void text8 (GC gc, int x, int y, String s, int delta, Font font) {
    poly_text8 (gc, x, y, new Text [] {new Text (s, delta, font)});
  }


  /**
   * @see #text(GC, int, int, String, int, Font)
   */
  public void text (GC gc, int x, int y, String s) {
    poly_text8 (gc, x, y, new Text [] {new Text (s, 0, null)});
  }
}

