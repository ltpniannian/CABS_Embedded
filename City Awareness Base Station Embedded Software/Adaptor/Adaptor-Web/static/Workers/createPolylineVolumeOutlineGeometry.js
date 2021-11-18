define(["./when-54c2dc71","./Check-6c0211bc","./Math-fc8cecf5","./Cartesian2-bddc1162","./Transforms-43808565","./RuntimeError-2109023a","./WebGLConstants-76bb35d1","./ComponentDatatype-6d99a1ee","./GeometryAttribute-49698167","./GeometryAttributes-4fcfcf40","./IndexDatatype-53503fee","./IntersectionTests-60a97ecf","./Plane-c946480f","./arrayRemoveDuplicates-ebc732b0","./BoundingRectangle-6504da58","./EllipsoidTangentPlane-9a2d3381","./EllipsoidRhumbLine-c704bf4c","./PolygonPipeline-c425d4a8","./PolylineVolumeGeometryLibrary-412b539f","./EllipsoidGeodesic-30fae80b","./PolylinePipeline-0cb75e10"],function(c,e,a,d,y,i,n,h,f,g,m,t,r,o,l,s,p,u,v,E,P){"use strict";function _(e){var i=(e=c.defaultValue(e,c.defaultValue.EMPTY_OBJECT)).polylinePositions,n=e.shapePositions;this._positions=i,this._shape=n,this._ellipsoid=d.Ellipsoid.clone(c.defaultValue(e.ellipsoid,d.Ellipsoid.WGS84)),this._cornerType=c.defaultValue(e.cornerType,v.CornerType.ROUNDED),this._granularity=c.defaultValue(e.granularity,a.CesiumMath.RADIANS_PER_DEGREE),this._workerName="createPolylineVolumeOutlineGeometry";var t=1+i.length*d.Cartesian3.packedLength;t+=1+n.length*d.Cartesian2.packedLength,this.packedLength=t+d.Ellipsoid.packedLength+2}_.pack=function(e,i,n){var t;n=c.defaultValue(n,0);var a=e._positions,r=a.length;for(i[n++]=r,t=0;t<r;++t,n+=d.Cartesian3.packedLength)d.Cartesian3.pack(a[t],i,n);var o=e._shape,r=o.length;for(i[n++]=r,t=0;t<r;++t,n+=d.Cartesian2.packedLength)d.Cartesian2.pack(o[t],i,n);return d.Ellipsoid.pack(e._ellipsoid,i,n),n+=d.Ellipsoid.packedLength,i[n++]=e._cornerType,i[n]=e._granularity,i};var b=d.Ellipsoid.clone(d.Ellipsoid.UNIT_SPHERE),k={polylinePositions:void 0,shapePositions:void 0,ellipsoid:b,height:void 0,cornerType:void 0,granularity:void 0};_.unpack=function(e,i,n){i=c.defaultValue(i,0);for(var t=e[i++],a=new Array(t),r=0;r<t;++r,i+=d.Cartesian3.packedLength)a[r]=d.Cartesian3.unpack(e,i);t=e[i++];var o=new Array(t);for(r=0;r<t;++r,i+=d.Cartesian2.packedLength)o[r]=d.Cartesian2.unpack(e,i);var l=d.Ellipsoid.unpack(e,i,b);i+=d.Ellipsoid.packedLength;var s=e[i++],p=e[i];return c.defined(n)?(n._positions=a,n._shape=o,n._ellipsoid=d.Ellipsoid.clone(l,n._ellipsoid),n._cornerType=s,n._granularity=p,n):(k.polylinePositions=a,k.shapePositions=o,k.cornerType=s,k.granularity=p,new _(k))};var C=new l.BoundingRectangle;return _.createGeometry=function(e){var i=e._positions,n=o.arrayRemoveDuplicates(i,d.Cartesian3.equalsEpsilon),t=e._shape,t=v.PolylineVolumeGeometryLibrary.removeDuplicatesFromShape(t);if(!(n.length<2||t.length<3)){u.PolygonPipeline.computeWindingOrder2D(t)===u.WindingOrder.CLOCKWISE&&t.reverse();var a=l.BoundingRectangle.fromPoints(t,C);return function(e,i){var n=new g.GeometryAttributes;n.position=new f.GeometryAttribute({componentDatatype:h.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:e});var t=i.length,a=n.position.values.length/3,r=e.length/3/t,o=m.IndexDatatype.createTypedArray(a,2*t*(1+r)),l=0,s=0,p=s*t;for(u=0;u<t-1;u++)o[l++]=u+p,o[l++]=u+p+1;for(o[l++]=t-1+p,o[l++]=p,p=(s=r-1)*t,u=0;u<t-1;u++)o[l++]=u+p,o[l++]=u+p+1;for(o[l++]=t-1+p,o[l++]=p,s=0;s<r-1;s++)for(var c=t*s,d=c+t,u=0;u<t;u++)o[l++]=u+c,o[l++]=u+d;return new f.Geometry({attributes:n,indices:m.IndexDatatype.createTypedArray(a,o),boundingSphere:y.BoundingSphere.fromVertices(e),primitiveType:f.PrimitiveType.LINES})}(v.PolylineVolumeGeometryLibrary.computePositions(n,t,a,e,!1),t)}},function(e,i){return c.defined(i)&&(e=_.unpack(e,i)),e._ellipsoid=d.Ellipsoid.clone(e._ellipsoid),_.createGeometry(e)}});
