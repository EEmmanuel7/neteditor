/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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

package org.arakhne.neteditor.io.gxl ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.arakhne.afc.progress.Progression;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ModelObjectView;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.io.FileCollection;
import org.arakhne.neteditor.io.VectorialExporter;


/** This class permits to export the
 *  <strong>graph-model</strong> into the GXL format.
 *  <p>
 *  The exporter never exports the figures. Only the graph
 *  structure is exporter.
 *  <p>
 *  GXL (Graph eXchange Language) is designed to be a standard exchange
 *  format for graphs. GXL is an XML sublanguage and the syntax is 
 *  given by a XML DTD (Document Type Definition). This exchange format 
 *  offers an adaptable and flexible means to support interoperability 
 *  between graph-based tools.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://www.gupro.de/GXL/"
 */
public class GXLExporter implements VectorialExporter {

	private boolean exportShadows = false;
	private Progression taskProgression = null;
	private FileCollection fileCollection = null;

	/**
	 */
	public GXLExporter() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progression getProgression() {
		return this.taskProgression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProgression(Progression model) {
		Progression old = this.taskProgression;
		this.taskProgression = model;
		if (old!=null && this.taskProgression!=null) {
			this.taskProgression.setProperties(
					old.getValue(),
					old.getMinimum(),
					old.getMaximum(),
					old.isAdjusting(),
					old.getComment());
			this.taskProgression.setIndeterminate(old.isIndeterminate());
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowSupported() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSpecificationCompliant() {
		return true;
	}

	@Override
	public FileCollection getFileCollection() {
		return this.fileCollection;
	}
	
	@Override
	public void setFileCollection(FileCollection c) {
		this.fileCollection = c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?, ?, ?, ?>> void write(
			File output, G graph,
			ViewComponentContainer<?, G> container) throws IOException {
		FileOutputStream fos = new FileOutputStream(output);
		try {
			write(fos, graph, container);
		}
		finally {
			fos.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(File output, Collection<? extends Figure> figures)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(output);
		try {
			write(fos, figures);
		}
		finally {
			fos.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?, ?, ?, ?>> void write(OutputStream output,
			G graph, ViewComponentContainer<?, G> container) throws IOException {
		GXLWriter writer = new GXLWriter();
		writer.setAnchorOutput(false);
		writer.setProgression(getProgression());
		writer.write(output, graph);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(OutputStream output, Collection<? extends Figure> figures)
			throws IOException {
		GXLWriter writer = new GXLWriter();
		writer.setAnchorOutput(false);
		writer.setProgression(getProgression());
	
		for(Figure figure : figures) {
			if (figure instanceof ModelObjectView<?>) {
				ModelObjectView<?> mov = (ModelObjectView<?>)figure;
				ModelObject mo = mov.getModelObject();
				if (mo instanceof Graph<?,?,?,?>) {
					writer.write(output, (Graph<?,?,?,?>)mo);
					return;
				}
				if (mo instanceof Node<?,?,?,?>) {
					writer.write(output, ((Node<?,?,?,?>)mo).getGraph());
					return;
				}
				if (mo instanceof Edge<?,?,?,?>) {
					writer.write(output, ((Edge<?,?,?,?>)mo).getGraph());
					return;
				}
				if (mo instanceof Anchor<?,?,?,?>) {
					Node<?,?,?,?> node = ((Anchor<?,?,?,?>)mo).getNode();
					if (node!=null) {
						writer.write(output, node.getGraph());
						return;
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isShadowExported() {
		return this.exportShadows;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setShadowExported(boolean export) {
		this.exportShadows = export;
	}

}
