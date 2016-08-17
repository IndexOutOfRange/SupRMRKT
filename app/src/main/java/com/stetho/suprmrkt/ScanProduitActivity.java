package com.stetho.suprmrkt;

import com.stetho.suprmrkt.model.AbstractScanActivity;

public class ScanProduitActivity extends AbstractScanActivity {
    @Override
    protected void processBarCode(String cab) {
        cabSuccessfullyTraite(cab);
    }
}
