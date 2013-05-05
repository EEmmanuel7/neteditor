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
package org.arakhne.neteditor.formalism;

import java.io.Serializable;

/** Provides standard property names.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface PropertyNames extends Serializable {

	/** */
	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	/** */
	public static final String PROPERTY_UUID = "uuid"; //$NON-NLS-1$
	/** */
	public static final String PROPERTY_LOCATION = "location"; //$NON-NLS-1$
	/** */
	public static final String PROPERTY_ANCHORLOCATION = "anchorLocation"; //$NON-NLS-1$

}
