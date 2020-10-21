package io.jenkins.update_center;

import com.alibaba.fastjson.annotation.JSONField;
import hudson.util.VersionNumber;
import io.jenkins.update_center.util.JavaSpecificationVersion;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An entry of a plugin in the update center metadata.
 *
 */
public class PluginUpdateCenterEntry {
    /**
     * Plugin artifact ID.
     */
    @JSONField(name = "name")
    public final String artifactId;
    /**
     * Latest version of this plugin.
     */
    private transient final HPI latestOffered;
    /**
     * Previous version of this plugin.
     */
    @CheckForNull
    private transient final HPI previousOffered;

    private PluginUpdateCenterEntry(String artifactId, HPI latestOffered, HPI previousOffered) {
        this.artifactId = artifactId;
        this.latestOffered = latestOffered;
        this.previousOffered = previousOffered;
    }

    public PluginUpdateCenterEntry(Plugin plugin) {
        this.artifactId = plugin.getArtifactId();
        HPI previous = null, latest = null;

        Iterator<HPI> it = plugin.getArtifacts().values().iterator();

        while (latest == null && it.hasNext()) {
            HPI h = it.next();
            try {
                h.getManifest();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to resolve "+h+". Dropping this version.",e);
                continue;
            }
            latest = h;
        }

        while (previous == null && it.hasNext()) {
            HPI h = it.next();
            try {
                h.getManifest();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to resolve "+h+". Dropping this version.",e);
                continue;
            }
            previous = h;
        }

        this.latestOffered = latest;
        this.previousOffered = previous == latest ? null : previous;
    }

    public PluginUpdateCenterEntry(HPI hpi) {
        this(hpi.artifact.artifactId, hpi,  null);
    }

    /**
     *  Historical name for the plugin documentation URL field.
     *
     *  Now always links to plugins.jenkins.io, which in turn uses
     *  {@link io.jenkins.update_center.json.PluginDocumentationUrlsRoot} to determine where the documentation is
     *  actually located.
     *
     * @return a URL
     */
    @JSONField
    public String getWiki() {
        return "https://plugins.jenkins.io/" + artifactId;
    }

    String getPluginUrl() throws IOException {
        return latestOffered.getPluginUrl();
    }

    @JSONField(name = "url")
    public URL getDownloadUrl() throws MalformedURLException {
        return latestOffered.getDownloadUrl();
    }

    @JSONField(name = "title")
    public String getName() throws IOException {
        return latestOffered.getName();
    }

    @JSONField(name = "defaultBranch")
    public String defaultBranchl() throws IOException {
        return latestOffered.getDefaultBranch();
    }

    @JSONField(name = "issuesUrl")
    public String getIssuesUrl() throws IOException {
        return latestOffered.getIssuesUrl();
    }


    public String getVersion() {
        return latestOffered.version;
    }

    public String getPreviousVersion() {
        return previousOffered == null? null : previousOffered.version;
    }

    public String getScm() throws IOException {
        return latestOffered.getScmUrl();
    }

    public String getRequiredCore() throws IOException {
        return latestOffered.getRequiredJenkinsVersion();
    }

    public String getCompatibleSinceVersion() throws IOException {
        return latestOffered.getCompatibleSinceVersion();
    }

    public String getMinimumJavaVersion() throws IOException {
        final JavaSpecificationVersion minimumJavaVersion = latestOffered.getMinimumJavaVersion();
        return minimumJavaVersion == null ? null : minimumJavaVersion.toString();
    }

    public String getBuildDate() {
        return latestOffered.getTimestampAsString();
    }

    public List<String> getLabels() throws IOException {
        return latestOffered.getLabels();
    }

    public List<HPI.Dependency> getDependencies() throws IOException {
        return latestOffered.getDependencies();
    }

    public String getSha1() throws IOException {
        return latestOffered.getDigests().sha1;
    }

    public String getSha256() throws IOException {
        return latestOffered.getDigests().sha256;
    }

    public String getGav() {
        return latestOffered.getGavId();
    }

    private static String fixEmptyAndTrim(String value) {
        if (value == null) {
            return null;
        }
        final String trim = value.trim();
        if (trim.length() == 0) {
            return null;
        }
        return trim;
    }

    public List<HPI.Developer> getDevelopers() throws IOException {
        final List<HPI.Developer> developers = latestOffered.getDevelopers();
        final String builtBy = fixEmptyAndTrim(latestOffered.getBuiltBy());
        if (developers.isEmpty() && builtBy != null) {
            return Collections.singletonList(new HPI.Developer(null, builtBy, null));
        }
        return developers;
    }

    public String getExcerpt() throws IOException {
        return latestOffered.getDescription();
    }

    public String getReleaseTimestamp() {
        return TIMESTAMP_FORMATTER.format(latestOffered.getTimestamp());
    }

    public String getPreviousTimestamp() {
        return previousOffered == null ? null : TIMESTAMP_FORMATTER.format(previousOffered.getTimestamp());
    }

    public int getPopularity() throws IOException {
        return Popularities.getInstance().getPopularity(artifactId);
    }

    public String getLatest() {
        final LatestPluginVersions instance = LatestPluginVersions.getInstance();
        if (instance == null) {
            return null;
        }
        final VersionNumber latestPublishedVersion = instance.getLatestVersion(artifactId);
        if (latestPublishedVersion == null || latestPublishedVersion.equals(latestOffered.getVersion())) {
            // only include latest version information if the currently published version isn't the latest
            return null;
        }
        return latestPublishedVersion.toString();
    }

    private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.00Z'", Locale.US);

    private static final Logger LOGGER = Logger.getLogger(PluginUpdateCenterEntry.class.getName());
}
