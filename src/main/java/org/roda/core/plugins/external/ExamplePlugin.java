/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.external;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roda.core.common.iterables.CloseableIterable;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.common.RodaConstants.PreservationEventType;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RODAException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.LiteOptionalWithCause;
import org.roda.core.data.v2.common.OptionalWithCause;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.AIPState;
import org.roda.core.data.v2.ip.File;
import org.roda.core.data.v2.ip.Representation;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.PluginParameter;
import org.roda.core.data.v2.jobs.PluginType;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.data.v2.jobs.Report.PluginState;
import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.AbstractPlugin;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginException;
import org.roda.core.plugins.RODAObjectProcessingLogic;
import org.roda.core.plugins.orchestrate.JobPluginInfo;
import org.roda.core.plugins.plugins.PluginHelper;
import org.roda.core.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplePlugin extends AbstractPlugin<AIP> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePlugin.class);

  private static Map<String, PluginParameter> pluginParameters = new HashMap<>();
  static {
    // ADD PARAMETERS
  }

  public ExamplePlugin() {
    super();
  }

  @Override
  public void init() throws PluginException {
    // do nothing
  }

  @Override
  public void shutdown() {
    // do nothing
  }

  @Override
  public String getName() {
    // Get from pom.xml <name>
    return getClass().getPackage().getImplementationTitle();
  }

  @Override
  public String getDescription() {
    return "Description of example plugin";
  }

  @Override
  public String getVersionImpl() {
    // Get from pom.xml <version>
    return getClass().getPackage().getImplementationVersion();
  }

  @Override
  public Report execute(IndexService index, ModelService model, StorageService storage,
    List<LiteOptionalWithCause> liteList) throws PluginException {
    return PluginHelper.processObjects(this, new RODAObjectProcessingLogic<AIP>() {
      @Override
      public void process(IndexService index, ModelService model, StorageService storage, Report report, Job cachedJob,
        JobPluginInfo jobPluginInfo, Plugin<AIP> plugin, AIP object) {
        processAIP(model, index, storage, report, jobPluginInfo, cachedJob, (AIP) object);
      }
    }, index, model, storage, liteList);
  }

  protected void processAIP(ModelService model, IndexService index, StorageService storage, Report report,
    JobPluginInfo jobPluginInfo, Job job, AIP aip) {
    Report reportItem = PluginHelper.initPluginReportItem(this, aip.getId(), AIP.class, AIPState.INGEST_PROCESSING);
    PluginHelper.updatePartialJobReport(this, model, reportItem, false, job);
    PluginState reportState = PluginState.SUCCESS;

    try {
      for (Representation representation : aip.getRepresentations()) {
        executeOnRepresentation(model, index, storage, representation);
      }

      jobPluginInfo.incrementObjectsProcessed(reportState);
      reportItem.setPluginState(reportState);

      if (!reportState.equals(PluginState.FAILURE)) {
        reportItem.setHtmlPluginDetails(true).setPluginDetails("Details");
      }

    } catch (RODAException | RuntimeException e) {
      LOGGER.error("Error processing AIP " + aip.getId() + ": " + e.getMessage(), e);
      reportItem.setPluginState(PluginState.FAILURE).setPluginDetails(e.getMessage());
      jobPluginInfo.incrementObjectsProcessedWithFailure();
    } finally {
      reportItem.addPluginDetails("outcome");
      report.addReport(reportItem);
      PluginHelper.updatePartialJobReport(this, model, reportItem, true, job);
    }
  }

  private void executeOnRepresentation(ModelService model, IndexService index, StorageService storage,
    Representation representation)
    throws NotFoundException, GenericException, RequestNotValidException, AuthorizationDeniedException {
    LOGGER.debug("Processing representation {}", representation);
    boolean recursive = true;
    try (CloseableIterable<OptionalWithCause<File>> allFiles = model.listFilesUnder(representation.getAipId(),
      representation.getId(), recursive)) {

      for (OptionalWithCause<File> oFile : allFiles) {
        if (oFile.isPresent()) {
          File file = oFile.get();
          LOGGER.debug("Processing file {}", file);
        } else {
          LOGGER.error("Cannot process representation file", oFile.getCause());
        }
      }
    } catch (IOException e) {
      throw new GenericException("Could not list files", e);
    }

  }

  @Override
  public PluginType getType() {
    return PluginType.AIP_TO_AIP;
  }

  @Override
  public Plugin<AIP> cloneMe() {
    return new ExamplePlugin();
  }

  @Override
  public boolean areParameterValuesValid() {
    return true;
  }

  @Override
  public PreservationEventType getPreservationEventType() {
    return PreservationEventType.DIGITAL_SIGNATURE_VALIDATION;
  }

  @Override
  public String getPreservationEventDescription() {
    return "Checked if ...";
  }

  @Override
  public String getPreservationEventSuccessMessage() {
    return "... with success.";
  }

  @Override
  public String getPreservationEventFailureMessage() {
    return "Failed to ...";
  }

  @Override
  public Report beforeAllExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report afterAllExecute(IndexService index, ModelService model, StorageService storage) throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public List<String> getCategories() {
    return Arrays.asList(RodaConstants.PLUGIN_CATEGORY_EXPERIMENTAL);
  }

  @Override
  public List<Class<AIP>> getObjectClasses() {
    return Arrays.asList(AIP.class);
  }

}
