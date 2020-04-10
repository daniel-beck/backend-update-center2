package org.jvnet.hudson.update_center.wrappers;

import hudson.util.VersionNumber;
import org.jvnet.hudson.update_center.BaseMavenRepository;
import org.jvnet.hudson.update_center.HPI;
import org.jvnet.hudson.update_center.JenkinsWar;
import org.jvnet.hudson.update_center.MavenRepository;
import org.jvnet.hudson.update_center.Plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Delegating {@link BaseMavenRepository} to limit the data to the subset compatible with the specific version.
 */
public class VersionCappedMavenRepository extends MavenRepositoryWrapper {

    /**
     * Version number to cap. We only report plugins that are compatible with this core version.
     */
    private final VersionNumber capPlugin;

    /**
     * Version number to cap core. We only report core versions as high as this.
     */
    private final VersionNumber capCore;

    public VersionCappedMavenRepository(MavenRepository base, VersionNumber capPlugin, VersionNumber capCore) {
        setBaseRepository(base);
        this.capPlugin = capPlugin;
        this.capCore = capCore;
    }

    @Override
    public TreeMap<VersionNumber, JenkinsWar> getHudsonWar() throws IOException {
        return new TreeMap<>(base.getHudsonWar().tailMap(capCore,true));
    }

    @Override
    public Collection<Plugin> listHudsonPlugins() throws IOException {
        Collection<Plugin> r = base.listHudsonPlugins();

        for (Iterator<Plugin> jtr = r.iterator(); jtr.hasNext();) {
            Plugin h = jtr.next();


            Map<VersionNumber, HPI> versionNumberHPIMap = new TreeMap<>(VersionNumber.DESCENDING);

            for (Iterator<Entry<VersionNumber, HPI>> itr = h.getArtifacts().entrySet().iterator(); itr.hasNext();) {
                Entry<VersionNumber, HPI> e =  itr.next();
                if (capPlugin == null) {
                    // no cap
                    versionNumberHPIMap.put(e.getKey(), e.getValue());
                    if (versionNumberHPIMap.size() >= 2) {
                        break;
                    }
                    continue;
                }
                try {
                    VersionNumber v = new VersionNumber(e.getValue().getRequiredJenkinsVersion());
                    if (v.compareTo(capPlugin)<=0) {
                        versionNumberHPIMap.put(e.getKey(), e.getValue());
                        if (versionNumberHPIMap.size() >= 2) {
                            break;
                        }
                        continue;
                    }
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }

            h.getArtifacts().entrySet().retainAll(versionNumberHPIMap.entrySet());

            if (h.getArtifacts().isEmpty())
                jtr.remove();
        }

        return r;
    }
}
