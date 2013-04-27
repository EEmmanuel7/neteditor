/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.android.actionmode ;

import java.util.UUID;

import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.base.BaseMode;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;

/** ModeManager keeps track of all the 
 *  {@link ActionMode Modes} for a given workspace.
 *  Events are passed to the Modes for handling.  The submodes are
 *  prioritized according to their order on a stack, i.e., the last
 *  Mode added gets the first chance to handle an Event.  
 *  <p>
 *  This ModeManager takes into account an exclusive Mode. An exclusive
 *  mode is a {@link ActionMode Mode} was received all events before the
 *  stacked modes. If an event was not eated by the exclusive mode,
 *  the exclusive mode was destroy and the event was ignored.
 * 
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ActionModeManager extends org.arakhne.afc.ui.actionmode.ActionModeManager<Figure,DroidViewGraphics2D,Color>
implements FigureActionModeManager {

	/** Size of the selection frame.
	 */
	public static final int SELECTION_FRAME_SIZE = 8;

	/** Size of the inflating of the repaint rectangle
	 */
	public static final int REPAINTING_INFLATING_SIZE = 16;
	
	/**
	 * @param viewID is the identifier of the view associated to this manager.
	 * @param component is a reference to the component that is 
	 * containing this mode manager.
	 */
	public ActionModeManager(UUID viewID, ActionModeOwner component) {
		super(viewID, component);
	}
	
	@Override
	public ActionModeOwner getModeManagerOwner() {
		return (ActionModeOwner)super.getModeManagerOwner();
	}

	@Override
	public void setSelectionMode(SelectionMode mode) {
		BaseMode baseMode = getModeFromBottom(BaseMode.class);
		if (baseMode!=null) {
			baseMode.setSelectionMode(mode);
		}
	}

	@Override
	public SelectionMode getSelectionMode() {
		BaseMode baseMode = getModeFromBottom(BaseMode.class);
		if (baseMode!=null) {
			return baseMode.getSelectionMode();
		}
		return SelectionMode.SINGLE;
	}

	@Override
	public void startMode(ActionMode<Figure, DroidViewGraphics2D, Color> mode) {
		beginMode(mode);
	}
		
}
