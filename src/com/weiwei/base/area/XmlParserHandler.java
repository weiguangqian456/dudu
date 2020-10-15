package com.weiwei.base.area;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class XmlParserHandler extends DefaultHandler {

    /**
     * �洢���еĽ�������
     */
    private List<ProvinceModel> provinceList = new ArrayList<ProvinceModel>();

    public XmlParserHandler() {

    }

    public List<ProvinceModel> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    ProvinceModel provinceModel = new ProvinceModel();
    CityModel cityModel = new CityModel();
    DistrictModel districtModel = new DistrictModel();
    TownModel townModel = new TownModel();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("province")) {
            provinceModel = new ProvinceModel();
            provinceModel.setName(attributes.getValue(0));
            provinceModel.setCityList(new ArrayList<CityModel>());
        } else if (qName.equals("city")) {
            cityModel = new CityModel();
            cityModel.setName(attributes.getValue(0));
            cityModel.setDistrictList(new ArrayList<DistrictModel>());
        } else if (qName.equals("district")) {
            districtModel = new DistrictModel();
            districtModel.setName(attributes.getValue(0));
//			districtModel.setZipcode(attributes.getValue(1));
            districtModel.setTownList(new ArrayList<TownModel>());
        } else if (qName.equals("town")) {
            townModel = new TownModel();
            townModel.setName(attributes.getValue(0));
            townModel.setZipcode(attributes.getValue(1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("town")) {
            districtModel.getTownList().add(townModel);
        } else if (qName.equals("district")) {
            cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals("city")) {
            provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
            provinceList.add(provinceModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

}
