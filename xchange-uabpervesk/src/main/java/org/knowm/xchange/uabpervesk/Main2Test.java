package org.knowm.xchange.uabpervesk;

import com.prowidesoftware.swift.model.mx.MxCamt05300102;
import com.prowidesoftware.swift.utils.Lib;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.dto.account.FundingRecord;

public class Main2Test {

  public static void main(String[] args) throws IOException {
    MxCamtConverter mxCamtConverter = new MxCamtConverter();
    List<FundingRecord> fr = mxCamtConverter.convertFundingRecords(Lib.readFile("C:\\temp\\bankera.xml"));
    System.out.println("hi");

  }

}
