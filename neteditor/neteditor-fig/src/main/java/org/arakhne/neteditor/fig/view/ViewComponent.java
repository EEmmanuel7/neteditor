/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */
package org.arakhne.neteditor.fig.view;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.util.PropertyOwner;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** This interface represents a component of a view.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ViewComponent extends Serializable, Comparable<ViewComponent>, PropertyOwner {

	/** Repaint the component.
	 * 
	 * @param container
	 */
	public void setViewComponentContainer(ViewComponentContainer<?,?> container);

	/** Repaint the component.
	 * 
	 * @param boundsChanged indicates if the repaint is due to a change
	 * in the bounds; or not.
	 */
	public void repaint(boolean boundsChanged);
	
	/** Replies the UUID of the view that is enclosing this figure.
	 * 
	 * @return the UUID of the view that is enclosing this figure.
	 */
	public UUID getViewUUID();

	/** Change the UUID of the view that is enclosing this figure.
	 * 
	 * @param id is the UUID of the view that is enclosing this figure.
	 */
	public void setViewUUID(UUID id);

	/** Replies the UUID of this figure.
	 * 
	 * @return the UUID of this figure.
	 */
	public UUID getUUID();
	
	/** Set the UUID of this figure.
	 * 
	 * @param id is the UUID of this figure.
	 */
	public void setUUID(UUID id);

	/** Replies the URL that permits to download the specified
	 * of the metamodel for this element.
	 * 
	 * @return the url of the schema for this figure. If <code>null</code>
	 * the default specification from NetEditor is assumed.
	 */
	public URL getMetamodelSpecification();

	/** Add listener on changes.
	 * 
	 * @param listener
	 */
	public void addViewComponentPropertyChangeListener(ViewComponentPropertyChangeListener listener);

	/** Add listener on changes.
	 * 
	 * @param listener
	 */
	public void removeViewComponentPropertyChangeListener(ViewComponentPropertyChangeListener listener);

	/** Add listener on changes.
	 * 
	 * @param listener
	 */
	public void addViewComponentChangeListener(ViewComponentChangeListener listener);

	/** Add listener on changes.
	 * 
	 * @param listener
	 */
	public void removeViewComponentChangeListener(ViewComponentChangeListener listener);

	/** Add listener on repaint requests.
	 * 
	 * @param listener
	 */
	public void addViewComponentRepaintListener(ViewComponentLayoutListener listener);

	/** Add listener on repaint requests.
	 * 
	 * @param listener
	 */
	public void removeViewComponentRepaintListener(ViewComponentLayoutListener listener);

	/** Clean this Fig and all its internal temprary fields.
	 */
	public void cleanUp() ;

	/** Method to paint this Fig.
	 *
	 * @param g the graphic context.
	 */
	public void paint(ViewGraphics2D g) ;

	/** Replies the clipping shape for this node according to the
	 * specified bounds of the figure.
	 * The specified bounds of the figure may be different of
	 * the current bounds of the figure. So that it is
	 * not recommanded to invoke {@link #getBounds()},
	 * {@link #getX()}, {@link #getY()}, {@link #getWidth()},
	 * and {@link #getHeight()}.
	 * 
	 * @param figureBounds are the bounds of the figure to assume to compute the clipping object.
	 * @return the clipping shape, or <code>null</code> to use the default.
	 */
	public Shape2f getClip(Rectangle2f figureBounds);

	/** Reply the color of the lines.
	 *
	 * @return the color of the lines.
	 */
	public Color getLineColor();

	/** Reply the color of the filling areas.
	 *
	 * @return the color of the filling areas.
	 */
	public Color getFillColor();

	/** Reply the color of the shadows.
	 *
	 * @return the color of the shadows.
	 */
	public Color getShadowColor();

	/** Reply the color of the background.
	 *
	 * @return the color of the background.
	 */
	public Color getBackgroundColor();

	/** Reply <code>true</code> if given coords are in the bounds of this Fig.
	 * This function takes into account only the bounds of the figure.
	 * If you are interested in a click in the shape of the figure,
	 * see {@link #hit(float, float, float)}
	 *
	 * @param x horizontal coord.
	 * @param y vertical coord.
	 * @return <code>true</code> if the point 
	 *         (<var>x</var>,<var>y</var>) is in this Fig.
	 * @see #hit(float, float, float)
	 */
	public boolean contains(float x, float y) ;

	/** Reply <code>true</code> if the given rectangle is inside this component.
	 *
	 *  @param r a rectangle
	 *  @return <code>true</code> if the given rectangle is
	 *  inside this component; otherwise <code>false</code>.
	 */    
	public boolean contains(Rectangle2f r) ;

	/** Reply <code>true</code> if given coords may correspond
	 * to a position of the mouse cursor that permits to
	 * hit this figure.
	 * This function should take into account the shape of 
	 * the figure, and not only the bounds as for {@link #contains(float, float)}
	 *
	 * @param x horizontal coord.
	 * @param y vertical coord.
	 * @param epsilon is the error amount allowed for the hitting test.
	 * @return <code>true</code> if the point 
	 *         (<var>x</var>,<var>y</var>) is in this Fig.
	 * @see #contains(float, float)
	 */
	public boolean hit(float x, float y, float epsilon) ;

	/** Set the width of this Fig.
	 *
	 * @param width width.
	 */
	public void setWidth(float width) ;

	/** Set the height of this Fig.
	 *
	 * @param height height.
	 */
	public void setHeight(float height) ;

	/** Reply the position and the dimension of the area that
	 *  will be refresh when the Fig was damaged.
	 *  <p>
	 *  The returned rectangle must always be in the
	 *  screen coordinate system. No relative/local
	 *  coordinate system must be used, in opposite of
	 *  {@link #getBounds()} that is allowing
	 *  relative coordinates.
	 *  <p>
	 *  Note that the coordinates replied by this function
	 *  are always global (eg, screen coordinates).
	 *
	 * @return the damaged area.
	 */
	public Rectangle2f getDamagedBounds() ;

	/** Reply the minimal height of the figure.
	 * 
	 * @return the height
	 */
	public float getMinimalHeight() ;

	/** Reply the minimal width of the figure .
	 * 
	 * @return the width
	 */
	public float getMinimalWidth() ;

	/** Reply the minimal dimension of the figure .
	 * 
	 * @return the dimension 
	 */
	public Dimension getMinimalDimension() ;

	/** Set the minimal height of the figure.
	 * 
	 * @param height is the minimal height
	 */
	public void setMinimalHeight(float height) ;

	/** Set the minimal width of the figure .
	 * 
	 * @param width is the minimal width
	 */
	public void setMinimalWidth(float width) ;

	/** Set the minimal dimension of the figure .
	 * 
	 * @param dimension is the minimal width
	 */
	public void setMinimalDimension(Dimension dimension) ;

	/** Set the minimal dimension of the figure .
	 * 
	 * @param width is the minimal width
	 * @param height is the minimal height
	 */
	public void setMinimalDimension(float width, float height) ;

	/** Reply the maximal height of the figure.
	 * 
	 * @return the height
	 */
	public float getMaximalHeight() ;

	/** Reply the maximal width of the figure .
	 * 
	 * @return the width
	 */
	public float getMaximalWidth() ;

	/** Reply the maximal dimension of the figure .
	 * 
	 * @return the dimension
	 */
	public Dimension getMaximalDimension() ;

	/** Set the maximal height of the figure.
	 * 
	 * @param height is the maximal height
	 */
	public void setMaximalHeight(float height) ;

	/** Set the maximal width of the figure .
	 * 
	 * @param width is the maximal width
	 */
	public void setMaximalWidth(float width) ;

	/** Set the maximal dimension of the figure .
	 * 
	 * @param dimension is the maximal dimension
	 */
	public void setMaximalDimension(Dimension dimension) ;

	/** Set the maximal dimension of the figure .
	 * 
	 * @param width is the maximal width
	 * @param height is the maximal height
	 */
	public void setMaximalDimension(float width, float height) ;
	
	/** Reply <code>true</code> if the object intersects the given rectangle.
	 *
	 *  @param r a rectangle
	 *  @return <code>true</code> if the figure intersects
	 *          the given rectangle. otherwise <code>false</code>
	 */    
	public boolean intersects(Shape2f r) ;

	/** Reply a copy of the position and the dimension of this Fig.
	 *  <p>
	 *  Note that the coordinates replied by this function
	 *  may be global (eg, screen coordinates) or local
	 *  (eg, coordinates to the origin of a figure).
	 *
	 * @return position and dimension.
	 */
	public Rectangle2f getBounds() ;
	
	/** Change the position and the dimension of this Fig.
	 *
	 * @param x horizontal position of this Fig.
	 * @param y vertical position of this Fig.
	 * @param width width of this Fig.
	 * @param height height of this Fig.
	 */
	public void setBounds(float x, float y, float width, float height) ;

	/** Change the position and the dimension of this Fig.
	 *
	 * @param bounds
	 */
	public void setBounds(Rectangle2f bounds) ;

	/** Change the size of this Fig.
	 *
	 * @param width width of this Fig.
	 * @param height height of this Fig.
	 */
	public void setSize(float width, float height) ;

	/** Reply the width of this Fig.
	 *
	 * @return width.
	 */
	public float getWidth() ;

	/** Reply the height of this Fig.
	 *
	 * @return height.
	 */
	public float getHeight() ;

	/** Reply the size of this Fig.
	 *
	 * @return the size of this Fig.
	 */
	public Dimension getSize() ;

	/** Replies the foreground color associated to selection.
	 * 
	 * @return the foreground selection color
	 */
	public Color getForegroundSelectionColor();

	/** Replies the background color associated to selection.
	 * 
	 * @return the background selection color
	 */
	public Color getBackgroundSelectionColor();

	/** Reply the horizontal position of this Fig.
	 *
	 * @return horizontal position.
	 */
	public float getX() ;

	/** Reply the vertical position of this Fig.
	 *
	 * @return vertical position.
	 */
	public float getY() ;

	/** Reply the position of this Fig.
	 *
	 * @return the position of this Fig.
	 */
	public Point2D getLocation() ;

	/** Change the position of this Fig.
	 *
	 * @param x the new horizontal position of this Fig.
	 * @param y the new vertical position of this Fig.
	 */
	public void setLocation(float x, float y) ;

	/** Move this Fig.
	 *
	 * @param dx horizontal movement dimension.
	 * @param dy vertical movement dimension.
	 */
	public void translate(float dx, float dy) ;

	/** Replies the name of the view component.
	 * 
	 * @return the name of the view component.
	 */
	public String getName();
	
	/** Set the name of the view component.
	 * 
	 * @param name is the name of the view component.
	 */
	public void setName(String name);

	/** Replies the icon of the view component.
	 * 
	 * @return the icon of the view component.
	 */
	public Image getIcon();
	
	/** Set the icon of the view component.
	 * 
	 * @param icon is the icon of the view component.
	 */
	public void setIcon(Image icon);

	/** Link this view component to another figure in the view.
	 * The associated figure moved according to the anchor point
	 * provided to it by this view component. 
	 * 
	 * @param figureId is the identifier for the associated figure.
	 * @param figure is the figure to add in the view.
	 * @return the coerced figure previously associated to the specified name.
	 */
	public CoercedFigure addAssociatedFigureIntoView(String figureId, CoercedFigure figure);

	/** Replies the associated figure with the specified identifier.
	 * The associated figure moved according to the anchor point
	 * provided to it by this view component. 
	 * 
	 * @param figureId is the identifier for the associated figure.
	 * @return the associated figure with the specified id; or
	 * <code>null</code>.
	 */
	public CoercedFigure getAssociatedFigureInView(String figureId);

	/** Replies the associated figures.
	 * The associated figure moved according to the anchor point
	 * provided to it by this view component. 
	 * 
	 * @return the associated figures; or
	 * <code>null</code>.
	 */
	public Map<String,CoercedFigure> getAssociatedFiguresInView();

	/** Notifies listeners about the removal of a figure from the view.
	 * The associated figure moved according to the anchor point
	 * provided to it by this view component. 
	 * 
	 * @param figureId is the identifier for the associated figure.
	 * @return the removed figure; or <code>null</code>.
	 */
	public CoercedFigure removeAssociatedFigureFromView(String figureId);

}
