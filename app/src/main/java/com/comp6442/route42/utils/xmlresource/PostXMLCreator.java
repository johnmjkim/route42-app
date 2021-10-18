package com.comp6442.route42.utils.xmlresource;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.SchedulablePost;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import timber.log.Timber;

public class PostXMLCreator {

    /**
     * Takes an object and outputs an xml formatted string of the object's members.
     * @return XML - Formatted string
     */
    public static Document createPostXML(SchedulablePost post) throws Exception {
        DocumentBuilderFactory postBuilderFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder postDocBuilder ;
        Document postDoc ;
        try {
            postDocBuilder = postBuilderFac.newDocumentBuilder();
            postDoc = postDocBuilder.newDocument();
            Element rootEl  = postDoc.createElementNS("", "ActivityPost");
            postDoc.appendChild(rootEl);
            rootEl.appendChild(createTextElement(postDoc, "uid", post.getUid()));
            rootEl.appendChild(createTextElement(postDoc, "profilePicUrl", post.getProfilePicUrl()));
            rootEl.appendChild(createTextElement(postDoc, "postDescription", post.getPostDescription()));
            rootEl.appendChild(createTextElement(postDoc, "userName", post.getUserName()));
            rootEl.appendChild(createTextElement(postDoc, "isPublic", String.valueOf(post.getIsPublic())));
            rootEl.appendChild(createTextElement(postDoc, "locationName", post.getLocationName()));
            rootEl.appendChild(createTextElement(postDoc, "Latitude", String.valueOf(post.getLatitude())));
            rootEl.appendChild(createTextElement(postDoc, "Longitude", String.valueOf( post.getLongitude())));
            return postDoc;
        }catch (Exception e) {
            Timber.e(e);
            throw new Exception(e);
        }
    }
    public static void saveLocalXMLFromDOM(Document dom, String storagePath) throws TransformerException {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            Timber.i("XMLstorage path" + storagePath);
            Result out = new StreamResult(new File(storagePath));
            t.transform(new DOMSource(dom), out);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

    }
    private static Node createTextElement(Document  doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

}
