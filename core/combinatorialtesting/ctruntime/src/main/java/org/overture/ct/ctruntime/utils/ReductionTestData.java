package org.overture.ct.ctruntime.utils;

import com.google.auto.value.AutoValue;

/**
 * Created by ldc on 05/02/16.
 */
@AutoValue
public abstract class ReductionTestData {
    public static ReductionTestData create(String name, TraceReductionInfo tri, int testCountResult){
        return new AutoValue_ReductionTestData(name,tri,testCountResult);
    }

    public abstract String name();
    public abstract TraceReductionInfo reductionInfo();
    public abstract int expectedResultSize();

}
