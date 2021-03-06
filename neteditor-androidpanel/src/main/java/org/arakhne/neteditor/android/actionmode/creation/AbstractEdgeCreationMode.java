/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
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
package org.arakhne.neteditor.android.actionmode.creation ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.anchor.AnchorFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;


/** This class implements a Mode that permits to
 * create decorations based on a collection of points.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEdgeCreationMode extends AbstractAndroidCreationMode {

	/** Constant that is used to detect click on coordinates
	 */
	public static final int CLICK_DISTANCE = 5;

	private final Rectangle2f damagedRectangle = new Rectangle2f();

	private AnchorFigure<?> startAnchor = null;
	private Edge<?,?,?,?> edge = null;

	private Path2f shape = null;

	private final int undoLabel;
	private String undoLabelBuffer = null;

	/** Construct a new AbstractEdgeCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 *  
	 *  @param undoLabel
	 */
	public AbstractEdgeCreationMode(int undoLabel) {
		super(true);
		this.undoLabel = undoLabel;
	}

	private static boolean isSameAnchor(AnchorFigure<?> a, AnchorFigure<?> b) {
		if (a==b) return true;
		if (a==null || b==null) return false;
		ModelObject oa = a.getModelObject();
		ModelObject ob = b.getModelObject();
		if (oa==ob) return true;
		if (oa==null || ob==null) return false;
		return oa.equals(ob);
	}

	private AnchorFigure<?> getPointedAnchor() {
		Figure fig = getPointedFigure();
		if (fig instanceof NodeFigure<?,?>) {
			NodeFigure<?,?> nodeFigure = (NodeFigure<?,?>)fig;
			Shape2f hitArea = getModeManager().getFigureHitArea();
			if (hitArea!=null) {
				return nodeFigure.getAnchorOn(hitArea);
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean canStartFrom(AnchorFigure<?> figure) {
		if (figure!=null) {
			Anchor anchor = figure.getModelObject();
			assert(anchor!=null);
			Edge newEdge = getCurrentEdge();
			assert(newEdge!=null);
			assert(this.startAnchor==null);
			return anchor.canConnectAsStartAnchor(newEdge, null);
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean canArriveTo(AnchorFigure<?> figure) {
		if (figure!=null) {
			Anchor anchor = figure.getModelObject();
			assert(anchor!=null);
			Edge newEdge = getCurrentEdge();
			assert(newEdge!=null);
			assert(this.startAnchor!=null);
			Anchor otherSide = this.startAnchor.getModelObject();
			assert(otherSide!=null);
			return anchor.canConnectAsEndAnchor(newEdge, otherSide);
		}
		return false;
	}

	private Edge<?,?,?,?> getCurrentEdge() {
		if (this.edge==null) {
			this.edge = createEdge();
		}
		return this.edge;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onModeActivated() {
		setExclusive(true);
		requestFocus();
		super.onModeActivated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanMode() {
		super.cleanMode();
		this.shape = null;
		this.edge = null;
		this.startAnchor = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(DroidViewGraphics2D g) {
		if (this.shape!=null) {
			Color border = getModeManagerOwner().getSelectionBackground();
			g.setOutlineColor(border);
			g.setInteriorPainted(false);
			g.setOutlineDrawn(true);
			g.draw(this.shape);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		if (this.startAnchor==null) {
			AnchorFigure<?> figure = getPointedAnchor();
			if (figure!=null && canStartFrom(figure)) {
				this.startAnchor = figure;
				event.consume();
			}
		}
		else {
			// Add a point in the path
			Point2D p = event.getPosition();
			updatePath(p.getX(), p.getY(), true);
			event.consume();
		}
	}
	
	private void updatePath(float x, float y, boolean lineTo) {
		Point2f lastPoint;
		if (this.shape==null) {
			this.damagedRectangle.set(this.startAnchor.getAbsoluteBounds());
			this.shape = new Path2f();
			lastPoint = new Point2f(
					this.damagedRectangle.getCenterX(),
					this.damagedRectangle.getCenterY());
			this.shape.moveTo(lastPoint.getX(), lastPoint.getY());
			this.shape.lineTo(x, y);
		}
		else {
			lastPoint = this.shape.getPointAt(this.shape.size()-2);
			if (lineTo)
				this.shape.lineTo(x, y);
			else
				this.shape.setLastPoint(x, y);
		}
		Rectangle2f newBounds = new Rectangle2f();
		newBounds.setFromCorners(
				lastPoint.getX(), lastPoint.getY(),
				x, y);
		Rectangle2f rep = newBounds.createUnion(this.damagedRectangle);
		this.damagedRectangle.set(newBounds);
		repaint(rep);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerDragged(ActionPointerEvent event) {
		if (this.startAnchor!=null) {
			// Update the last point in the path
			Point2D p = event.getPosition();
			updatePath(p.getX(), p.getY(), false);
			event.consume();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		if (this.startAnchor!=null) {
			AnchorFigure<?> currentAnchor = getPointedAnchor();
			if (currentAnchor!=null && canArriveTo(currentAnchor)
				&&
				(!isSameAnchor(this.startAnchor, currentAnchor)
				 ||(this.shape!=null && this.shape.size()>2))) { // Loop on the same anchor only if a point exists outside

				ActionModeOwner container = getModeManagerOwner();
				Graph<?,?,?,?> graph = container.getGraph();
				Edge<?,?,?,?> edge = getCurrentEdge();

				if (graph!=null && edge!=null) {
					Rectangle2f box = currentAnchor.getAbsoluteBounds();
					Point2D center = new Point2f(
							box.getCenterX(),
							box.getCenterY());
					if (this.shape==null) {
						box = this.startAnchor.getAbsoluteBounds();
						this.shape = new Path2f();
						this.shape.moveTo(box.getCenterX(), box.getCenterY());
						this.shape.lineTo(center.getX(), center.getY());
					}
					else {
						this.shape.setLastPoint(center.getX(), center.getY());
					}
					
					if (this.undoLabelBuffer==null) {
						this.undoLabelBuffer = container.getContext().getString(this.undoLabel);
					}
					
					Undo undoCmd = new Undo(
							this.undoLabelBuffer,
							getModeManager().getViewID(),
							graph,
							edge,
							this.startAnchor.getModelObject(),
							currentAnchor.getModelObject(),
							this.shape);
					undoCmd.doEdit();
					container.getUndoManager().add(undoCmd);

					finish();
				}
			}

			event.consume();
		}
	}

	/** Invoked to create an edge instance that is not already
	 * connected to anchors.
	 * 
	 * @return the edge instance.
	 */
	protected abstract Edge<?,?,?,?> createEdge();

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	private static class Undo extends AbstractUndoable {

		private static final long serialVersionUID = -3604234058112925898L;

		private final String label;
		private final UUID viewId;
		private final Graph graph;
		private final Edge edge;
		private final Anchor startAnchor;
		private final Anchor endAnchor;
		private final Path2f path;

		/**
		 * @param label
		 * @param viewId
		 * @param graph
		 * @param edge
		 * @param startAnchor
		 * @param endAnchor
		 * @param controlPoints
		 */
		public Undo(String label, UUID viewId, Graph<?,?,?,?> graph, Edge<?,?,?,?> edge, Anchor<?,?,?,?> startAnchor, Anchor<?,?,?,?> endAnchor, Path2f controlPoints) {
			this.label = label;
			this.viewId = viewId;
			this.graph = graph;
			this.edge = edge;
			this.startAnchor = startAnchor;
			this.endAnchor = endAnchor;
			this.path = controlPoints;
		}

		@Override
		public void doEdit() {
			this.graph.addEdge(this.edge);
			EdgeFigure<?> figure = this.edge.getViewBinding().getView(this.viewId, EdgeFigure.class);
			figure.setCtrlPoints(this.path.toCollection());
			this.edge.setStartAnchor(this.startAnchor);
			this.edge.setEndAnchor(this.endAnchor); 
		}

		@Override
		public void undoEdit() {
			this.graph.removeEdge(this.edge);
		}

		@Override
		public String getPresentationName() {
			return this.label;
		}

	}

}