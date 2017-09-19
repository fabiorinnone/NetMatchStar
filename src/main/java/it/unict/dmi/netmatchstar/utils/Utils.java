package it.unict.dmi.netmatchstar.utils;

import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by fabior on 19/09/17.
 */
public class Utils {

    public static boolean isApproximatePath(String s) {
        return 	s.startsWith(Common.APPROX_GT) ||  s.startsWith(Common.APPROX_GE) ||
                s.startsWith(Common.APPROX_EQ) ||  s.startsWith(Common.APPROX_LT) ||
                s.startsWith(Common.APPROX_LE);
    }

    public static String isNumber(String s) {
        if(s.startsWith(Common.GE))
            return Common.GE;
        else if(s.startsWith(Common.GT))
            return Common.GT;
        else if(s.startsWith(Common.LE))
            return Common.LE;
        else if(s.startsWith(Common.LT))
            return Common.LT;
        return Common.UNDEFINED;
    }

    public static String getNumber(String s) {
        int index = s.indexOf("=");
        if(s.charAt(1) == '=')
            return s.substring(2);
        else
            return s.substring(1);
    }

    //issue #18
    public static void createNetMatchStarStyle(CySwingAppAdapter adapter) {
        VisualMappingManager manager = adapter.getVisualMappingManager();
        VisualStyleFactory vsf = adapter.getVisualStyleFactory();
        Set<VisualStyle> visualStyles = manager.getAllVisualStyles();

        VisualStyle vs = null;

        boolean vsFound = false;
        Iterator<VisualStyle> iterator = visualStyles.iterator();
        VisualStyle currentVs = null;
        while(iterator.hasNext() && !vsFound) {
            currentVs = iterator.next();
            if (currentVs.getTitle().equals(Common.NETMATCH_STYLE))
                vsFound = true;
        }

        if (!vsFound)
            vs = vsf.createVisualStyle(Common.NETMATCH_STYLE);

        if (!visualStyles.contains(vs))
            manager.addVisualStyle(vs);
    }

    //issue #18
    public static VisualStyle getVisualStyle(CySwingAppAdapter adapter, String visualStyleName) {
        VisualMappingManager manager = adapter.getVisualMappingManager();
        VisualStyleFactory vsf = adapter.getVisualStyleFactory();
        Set<VisualStyle> visualStyles = manager.getAllVisualStyles();

        VisualStyle vs = null;

        boolean vsFound = false;
        Iterator<VisualStyle> iterator = visualStyles.iterator();
        VisualStyle currentVs = null;
        while(iterator.hasNext() && !vsFound) {
            currentVs = iterator.next();
            if (currentVs.getTitle().equals(visualStyleName))
                return currentVs;
        }

        return null;
    }
}
