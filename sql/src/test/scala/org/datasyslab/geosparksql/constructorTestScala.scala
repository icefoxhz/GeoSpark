/*
 * FILE: constructorTestScala.scala
 * Copyright (c) 2015 - 2018 GeoSpark Development Team
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.datasyslab.geosparksql

import com.vividsolutions.jts.geom.Geometry
import org.datasyslab.geospark.formatMapper.GeoJsonReader
import org.datasyslab.geospark.formatMapper.shapefileParser.ShapefileReader
import org.datasyslab.geosparksql.utils.Adapter

class constructorTestScala extends TestBaseScala {

  describe("GeoSpark-SQL Constructor Test") {
    it("Passed ST_Point") {

      var pointCsvDF = sparkSession.read.format("csv").option("delimiter", ",").option("header", "false").load(csvPointInputLocation)

      pointCsvDF.createOrReplaceTempView("pointtable")
      var pointDf = sparkSession.sql("select ST_Point(cast(pointtable._c0 as Decimal(24,20)), cast(pointtable._c1 as Decimal(24,20))) as arealandmark from pointtable")
      assert(pointDf.count() == 1000)

    }

    it ("Passed ST_Point by double") {
      val pointDf = sparkSession.sql("SELECT ST_Point(double(1.2345), 2.3456)")
      assert(pointDf.count() == 1)
    }

    it ("Passed ST_PolygonFromEnvelope") {
      val polygonDF = sparkSession.sql("select ST_PolygonFromEnvelope(double(1.234),double(2.234),double(3.345),double(3.345))")
      polygonDF.show(false)
      assert(polygonDF.count() == 1)
    }

    it("Passed ST_PointFromText") {
      var pointCsvDF = sparkSession.read.format("csv").option("delimiter", ",").option("header", "false").load(arealmPointInputLocation)
      pointCsvDF.createOrReplaceTempView("pointtable")
      pointCsvDF.show(false)

      var pointDf = sparkSession.sql("select ST_PointFromText(concat(_c0,',',_c1),',') as arealandmark from pointtable")
      assert(pointDf.count() == 121960)
    }

    it("Passed ST_GeomFromWKT") {
      var polygonWktDf = sparkSession.read.format("csv").option("delimiter", "\t").option("header", "false").load(mixedWktGeometryInputLocation)
      polygonWktDf.createOrReplaceTempView("polygontable")
      polygonWktDf.show()
      var polygonDf = sparkSession.sql("select ST_GeomFromWkt(polygontable._c0) as countyshape from polygontable")
      polygonDf.show(10)
      assert(polygonDf.count() == 100)
    }

    it("Passed ST_GeomFromText") {
      var polygonWktDf = sparkSession.read.format("csv").option("delimiter", "\t").option("header", "false").load(mixedWktGeometryInputLocation)
      polygonWktDf.createOrReplaceTempView("polygontable")
      polygonWktDf.show()
      var polygonDf = sparkSession.sql("select ST_GeomFromText(polygontable._c0) as countyshape from polygontable")
      polygonDf.show(10)
      assert(polygonDf.count() == 100)
    }
    
    it("Passed ST_GeomFromWKT multipolygon read as polygon bug") {
      val multipolygon =
        """'MULTIPOLYGON (((-97.143362 27.84948, -97.14051 27.849375, -97.13742 27.849375, -97.13647 27.851056, -97.136945 27.853788, -97.138728 27.855784, -97.141223 27.853158, -97.143362 27.84948)),
            ((-97.131954 27.894443, -97.131716 27.896018, -97.1212014 27.8937854, -97.113415 27.892132, -97.110206 27.890662, -97.110206 27.889191, -97.1104204 27.8890963, -97.112107 27.888351, -97.114247 27.886985, -97.11591 27.88583, -97.11698 27.885515, -97.1173397 27.8855439, -97.118287 27.88562, -97.1180218 27.8875761, -97.117931 27.888246, -97.118287 27.890662, -97.1187136 27.8907652, -97.125269 27.8923509, -97.129577 27.893393, -97.131954 27.894443)),
            ((-97.150493 27.874905, -97.150849 27.875851, -97.149067 27.877531, -97.146927 27.878267, -97.14467 27.880893, -97.143362 27.881523, -97.142055 27.881103, -97.14158 27.879527, -97.142412 27.878582, -97.143719 27.878372, -97.145383 27.876586, -97.147403 27.875641, -97.149423 27.874905, -97.150493 27.874905)),
            ((-97.277888 27.915016, -97.280673 27.916797, -97.277151 27.918885, -97.270902 27.924911, -97.268651 27.926005, -97.269181 27.927081, -97.266121 27.927604, -97.257 27.927545, -97.231476 27.927514, -97.230477 27.953057, -97.229337 27.954497, -97.227041 27.955697, -97.226871 27.956842, -97.227509 27.984419, -97.240412 27.991214, -97.249501 27.99119, -97.249808 28.00156, -97.249403 28.008394, -97.249352 28.015777, -97.246465 28.020607, -97.217976 28.006301, -97.214474 28.011763, -97.187227 27.974459, -97.185218 27.975922, -97.18445 27.978007, -97.18181 27.979354, -97.181351 27.980691, -97.179325 27.979937, -97.177906 27.981857, -97.180899 27.981241, -97.180019 27.983282, -97.177081 27.985091, -97.177068 27.987313, -97.175036 27.98849, -97.174192 27.991763, -97.175233 27.993887, -97.173479 27.996164, -97.170119 27.997582, -97.168713 27.996343, -97.165283 27.992159, -97.10562 27.956867, -97.098551 27.965841, -97.099903 27.963404, -97.095463 27.960391, -97.094531 27.960663, -97.095924 27.961514, -97.093476 27.964801, -97.096948 27.967277, -97.092467 27.964277, -97.093252 27.964707, -97.095581 27.961632, -97.0942561 27.9607887, -97.120902 27.923113, -97.129276 27.918726, -97.131359 27.916602, -97.131241 27.914922, -97.129933 27.912192, -97.131359 27.907046, -97.132342 27.905581, -97.133023 27.90253, -97.1338567 27.9011057, -97.136312 27.90439, -97.137131 27.902861, -97.138515 27.900826, -97.136995 27.899203, -97.13535 27.8978363, -97.135638 27.896964, -97.135671 27.896907, -97.1375368 27.8936862, -97.138133 27.892657, -97.141342 27.88667, -97.144432 27.883099, -97.147641 27.88646, -97.148829 27.8852, -97.1490339 27.884976, -97.151396 27.882393, -97.154652 27.876796, -97.1546812 27.8767587, -97.154642 27.877004, -97.161216 27.880646, -97.161268 27.880676, -97.162722 27.881532, -97.163644 27.882081, -97.164889 27.880906, -97.166941 27.879639, -97.170125 27.881547, -97.16912 27.882906, -97.167873 27.884568, -97.170064 27.885862, -97.181393 27.892565, -97.183688 27.889517, -97.197275 27.89754, -97.195021 27.900546, -97.197251 27.902384, -97.200396 27.906587, -97.195212 27.91354, -97.203996 27.913649, -97.20666 27.919866, -97.21019 27.918115, -97.217076 27.915506, -97.223506 27.912777, -97.220665 27.90783, -97.232103 27.908176, -97.232165 27.90647, -97.233391 27.906212, -97.233185 27.892319, -97.233476 27.89176, -97.234908 27.892503, -97.248829 27.899804, -97.277888 27.915016)))'"""
      val wkt = sparkSession.sql(
        s"""
           |SELECT ST_GeomFromWkt($multipolygon)
           |""".stripMargin)
      assert(wkt.first().getAs[Geometry](0).getGeometryType === "MultiPolygon")
    }
    
    it("Passed ST_GeomFromWKB") {
      var polygonWkbDf = sparkSession.read.format("csv").option("delimiter", "\t").option("header", "false").load(mixedWkbGeometryInputLocation)
      polygonWkbDf.createOrReplaceTempView("polygontable")
      polygonWkbDf.show()
      var polygonDf = sparkSession.sql("select ST_GeomFromWKB(polygontable._c0) as countyshape from polygontable")
      polygonDf.show(10)
      assert(polygonDf.count() == 100)
    }

    it("Passed ST_GeomFromGeoJSON") {
      val polygonJsonDf = sparkSession.read.format("csv").option("delimiter", "\t").option("header", "false").load(geojsonInputLocation)
      polygonJsonDf.createOrReplaceTempView("polygontable")
      polygonJsonDf.show()
      val polygonDf = sparkSession.sql("select ST_GeomFromGeoJSON(polygontable._c0) as countyshape from polygontable")
      polygonDf.show()
      assert(polygonDf.count() == 1000)
    }

    it("Passed GeoJsonReader to DataFrame") {
      var spatialRDD = GeoJsonReader.readToGeometryRDD(sparkSession.sparkContext, geojsonInputLocation)
      var spatialDf = Adapter.toDf(spatialRDD, sparkSession)
      spatialDf.show()
    }

    it("Read shapefile -> DataFrame > RDD -> DataFrame") {
      var spatialRDD = ShapefileReader.readToGeometryRDD(sparkSession.sparkContext, shapefileInputLocation)
      spatialRDD.analyze()
      var df = Adapter.toDf(spatialRDD, sparkSession)
      df.show
      assert (df.columns(1) == "STATEFP")
      import org.apache.spark.sql.functions.{callUDF, col}
      df = df.withColumn("geometry", callUDF("ST_GeomFromWKT", col("geometry")))
      df.show()
      var spatialRDD2 = Adapter.toSpatialRdd(df, "geometry")
      println(spatialRDD2.rawSpatialRDD.take(1).get(0).getUserData)
      Adapter.toDf(spatialRDD2, sparkSession).show()
    }
  }
}
