package com.ansyah.ardi.trackcar.Model;

import java.util.ArrayList;

/**
 * Created by ardi on 05/07/18.
 */

public class KoordinatModel {
    public String _id;
    public ArrayList<KoordinatObjek> koordinat;

    public KoordinatModel(String _id, ArrayList<KoordinatObjek> koordinat) {
        this._id = _id;
        this.koordinat = koordinat;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<KoordinatObjek> getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(ArrayList<KoordinatObjek> koordinat) {
        this.koordinat = koordinat;
    }
}
