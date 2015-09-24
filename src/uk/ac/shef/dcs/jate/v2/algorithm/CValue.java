package uk.ac.shef.dcs.jate.v2.algorithm;

import uk.ac.shef.dcs.jate.v2.JATEException;
import uk.ac.shef.dcs.jate.v2.feature.AbstractFeature;
import uk.ac.shef.dcs.jate.v2.feature.Containment;
import uk.ac.shef.dcs.jate.v2.feature.FrequencyTermBased;
import uk.ac.shef.dcs.jate.v2.model.JATETerm;
import uk.ac.shef.dcs.jate.v2.model.TermInfo;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

/**
 * Created by zqz on 19/09/2015.
 */
public class CValue extends Algorithm {
    private static final Logger LOG = Logger.getLogger(CValue.class.getName());

    protected int maxPerWorker = 1000;

    public CValue() {
    }

    public CValue(int maxTermsPerWorker) {
        this.maxPerWorker = maxTermsPerWorker;
    }

    @Override
    public List<JATETerm> execute(Set<String> candidates) throws JATEException {
        candidates.remove("");

        AbstractFeature feature = features.get(FrequencyTermBased.class.getName());
        validateFeature(feature, FrequencyTermBased.class);
        FrequencyTermBased fFeature = (FrequencyTermBased) feature;

        AbstractFeature feature2 = features.get(Containment.class.getName());
        validateFeature(feature2, Containment.class);
        Containment cFeature = (Containment) feature2;

        int cores = Runtime.getRuntime().availableProcessors();
        StringBuilder msg = new StringBuilder("Beginning computing CValue, cores=");
        msg.append(cores).append(", total terms=" + candidates.size()).append(",").
                append(" max terms per worker thread=").append(maxPerWorker);
        LOG.info(msg.toString());
        ForkJoinPool forkJoinPool = new ForkJoinPool(cores);
        CValueWorker worker = new CValueWorker(new ArrayList<>(candidates), maxPerWorker, fFeature,
                cFeature,
                termInfoCollector);
        List<JATETerm> result = forkJoinPool.invoke(worker);
        Collections.sort(result);
        return result;
    }
}
