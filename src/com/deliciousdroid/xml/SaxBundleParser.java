/*
 * PinDroid - http://code.google.com/p/PinDroid/
 *
 * Copyright (C) 2010 Matt Schmidt
 *
 * PinDroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * PinDroid is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PinDroid; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package com.deliciousdroid.xml;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import org.xml.sax.Attributes;

import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import com.deliciousdroid.providers.BundleContent.Bundle;

public class SaxBundleParser {

	private InputStream is;
	
    public SaxBundleParser(InputStream stream) {
    	is = stream;
    }

    public ArrayList<Bundle> parse() throws ParseException {
        final Bundle currentBundle = new Bundle();
        RootElement root = new RootElement("bundles");
        final ArrayList<Bundle> bundles = new ArrayList<Bundle>();

        root.getChild("bundle").setStartElementListener(new StartElementListener(){
            public void start(Attributes attributes) {
            	String tags = attributes.getValue("", "tags");
            	String name = attributes.getValue("", "name");
            	
            	if(tags != null) {
            		currentBundle.setTagString(tags);
            	}
            	if(name != null) {
            		currentBundle.setName(name);
            	}

            	bundles.add(currentBundle.copy());
            }
        });
        try {
            Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return bundles;
    }
}