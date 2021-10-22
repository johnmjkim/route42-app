package com.comp6442.route42.utils.xmlresource;

import static com.comp6442.route42.data.model.Post.getHashTagsFromTextInput;

import com.comp6442.route42.data.model.Point;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import timber.log.Timber;

/**
 * Implements SAX Default Parser
 */
public class PostXMLParser {

    private class PostParserHandler extends DefaultHandler {
        private Post post;
        private final Stack<String> elementStack = new Stack<String>();
        private List<Point> route = new ArrayList<>();
        private double longitude;
        private double latitude;
        private StringBuilder elementContents = new StringBuilder();
        @Override
        public void startDocument() {
            post = new Post();
        }

        /*
        When a start tag or end tag is encountered, the name of the tag is passed
        as a String to the startElement or the endElement method, as appropriate.
        When a start tag is encountered, any attributes it defines are also passed
        in an Attributes list.
        Characters found within the element are passed as an array of characters,
        along with the number of characters (length) and an offset into the array
        that points to the first character.
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            elementStack.push(localName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String value = elementContents.toString();
            if(value.length() == 0) return;
            switch (currentElement()) {
                case "route_point_latitude":
                    latitude = Double.parseDouble(value);
                    break;
                case "route_point_longitude":
                    longitude = Double.parseDouble(value);
                    break;
                case "route_point":
                    route.add(new Point(longitude, latitude));
                    break;
                case "route":
                    post.setRoute(route);
                    break;
                case "uid":
                    DocumentReference userRef = UserRepository.getInstance().getOne(value);
                    post.setUid(userRef);
                    break;
                case "isPublic":
                    post.setIsPublic( Integer.parseInt(value));
                    break;
                case "locationname":
                    post.setLocationName(value);
                    break;
                case "username":
                    post.setUserName(value);
                    break;
                case "profilepicurl":
                    post.setProfilePicUrl(value);
                    break;
                case "postdescription":
                    post.setPostDescription(value);
                    post.setHashtags(getHashTagsFromTextInput(value));
                    break;
                case "latitude":
                    post.setLatitude(Double.valueOf(value));
                    break;
                case "longitude":
                    post.setLongitude(Double.valueOf(value));
                    break;
            }
            elementContents = new StringBuilder();
        }

        @Override
        public void characters(char[] ch, int start, int length)  {
            String value = new String(ch, start, length);

            elementContents.append(value);
        }

        private String currentElement() {
            return this.elementStack.peek().toLowerCase();
        }

        public Post getPost() {
            return post;
        }
    }
    public PostXMLParser() {
    }

    /**
     * Parses the given XML file into a Post object
     * @param xmlFilePath absolute filepath
     */
    public Post parse (String xmlFilePath) {
        PostParserHandler handler = new PostParserHandler();
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader reader =   saxParser.getXMLReader();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new FileInputStream(xmlFilePath)));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            Timber.e(e);
        }
        return handler.getPost();
    }

}