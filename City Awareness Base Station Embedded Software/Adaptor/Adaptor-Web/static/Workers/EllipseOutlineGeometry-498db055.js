define(["exports","./when-54c2dc71","./Check-6c0211bc","./Math-fc8cecf5","./Cartesian2-bddc1162","./Transforms-43808565","./ComponentDatatype-6d99a1ee","./GeometryAttribute-49698167","./GeometryAttributes-4fcfcf40","./IndexDatatype-53503fee","./GeometryOffsetAttribute-7350d9af","./EllipseGeometryLibrary-cc7c5f49"],function(e,A,t,_,g,x,E,v,M,C,G,L){"use strict";var O=new g.Cartesian3,l=new g.Cartesian3;var S=new x.BoundingSphere,V=new x.BoundingSphere;function f(e){var t=(e=A.defaultValue(e,A.defaultValue.EMPTY_OBJECT)).center,i=A.defaultValue(e.ellipsoid,g.Ellipsoid.WGS84),r=e.semiMajorAxis,a=e.semiMinorAxis,n=A.defaultValue(e.granularity,_.CesiumMath.RADIANS_PER_DEGREE),o=A.defaultValue(e.height,0),s=A.defaultValue(e.extrudedHeight,o);this._center=g.Cartesian3.clone(t),this._semiMajorAxis=r,this._semiMinorAxis=a,this._ellipsoid=g.Ellipsoid.clone(i),this._rotation=A.defaultValue(e.rotation,0),this._height=Math.max(s,o),this._granularity=n,this._extrudedHeight=Math.min(s,o),this._numberOfVerticalLines=Math.max(A.defaultValue(e.numberOfVerticalLines,16),0),this._offsetAttribute=e.offsetAttribute,this._workerName="createEllipseOutlineGeometry"}f.packedLength=g.Cartesian3.packedLength+g.Ellipsoid.packedLength+8,f.pack=function(e,t,i){return i=A.defaultValue(i,0),g.Cartesian3.pack(e._center,t,i),i+=g.Cartesian3.packedLength,g.Ellipsoid.pack(e._ellipsoid,t,i),i+=g.Ellipsoid.packedLength,t[i++]=e._semiMajorAxis,t[i++]=e._semiMinorAxis,t[i++]=e._rotation,t[i++]=e._height,t[i++]=e._granularity,t[i++]=e._extrudedHeight,t[i++]=e._numberOfVerticalLines,t[i]=A.defaultValue(e._offsetAttribute,-1),t};var m=new g.Cartesian3,h=new g.Ellipsoid,y={center:m,ellipsoid:h,semiMajorAxis:void 0,semiMinorAxis:void 0,rotation:void 0,height:void 0,granularity:void 0,extrudedHeight:void 0,numberOfVerticalLines:void 0,offsetAttribute:void 0};f.unpack=function(e,t,i){t=A.defaultValue(t,0);var r=g.Cartesian3.unpack(e,t,m);t+=g.Cartesian3.packedLength;var a=g.Ellipsoid.unpack(e,t,h);t+=g.Ellipsoid.packedLength;var n=e[t++],o=e[t++],s=e[t++],u=e[t++],l=e[t++],d=e[t++],c=e[t++],p=e[t];return A.defined(i)?(i._center=g.Cartesian3.clone(r,i._center),i._ellipsoid=g.Ellipsoid.clone(a,i._ellipsoid),i._semiMajorAxis=n,i._semiMinorAxis=o,i._rotation=s,i._height=u,i._granularity=l,i._extrudedHeight=d,i._numberOfVerticalLines=c,i._offsetAttribute=-1===p?void 0:p,i):(y.height=u,y.extrudedHeight=d,y.granularity=l,y.rotation=s,y.semiMajorAxis=n,y.semiMinorAxis=o,y.numberOfVerticalLines=c,y.offsetAttribute=-1===p?void 0:p,new f(y))},f.createGeometry=function(e){if(!(e._semiMajorAxis<=0||e._semiMinorAxis<=0)){var t=e._height,i=e._extrudedHeight,r=!_.CesiumMath.equalsEpsilon(t,i,0,_.CesiumMath.EPSILON2);e._center=e._ellipsoid.scaleToGeodeticSurface(e._center,e._center);var a,n,o,s,u={center:e._center,semiMajorAxis:e._semiMajorAxis,semiMinorAxis:e._semiMinorAxis,ellipsoid:e._ellipsoid,rotation:e._rotation,height:t,granularity:e._granularity,numberOfVerticalLines:e._numberOfVerticalLines};return r?(u.extrudedHeight=i,u.offsetAttribute=e._offsetAttribute,s=function(e){var t=e.center,i=e.ellipsoid,r=e.semiMajorAxis,a=g.Cartesian3.multiplyByScalar(i.geodeticSurfaceNormal(t,O),e.height,O);S.center=g.Cartesian3.add(t,a,S.center),S.radius=r,a=g.Cartesian3.multiplyByScalar(i.geodeticSurfaceNormal(t,a),e.extrudedHeight,a),V.center=g.Cartesian3.add(t,a,V.center),V.radius=r;var n,o,s=L.EllipseGeometryLibrary.computeEllipsePositions(e,!1,!0).outerPositions,u=new M.GeometryAttributes({position:new v.GeometryAttribute({componentDatatype:E.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:L.EllipseGeometryLibrary.raisePositionsToHeight(s,e,!0)})}),s=u.position.values,l=x.BoundingSphere.union(S,V),d=s.length/3;A.defined(e.offsetAttribute)&&(o=new Uint8Array(d),o=e.offsetAttribute===G.GeometryOffsetAttribute.TOP?G.arrayFill(o,1,0,d/2):(n=e.offsetAttribute===G.GeometryOffsetAttribute.NONE?0:1,G.arrayFill(o,n)),u.applyOffset=new v.GeometryAttribute({componentDatatype:E.ComponentDatatype.UNSIGNED_BYTE,componentsPerAttribute:1,values:o}));var c=A.defaultValue(e.numberOfVerticalLines,16),c=_.CesiumMath.clamp(c,0,d/2),p=C.IndexDatatype.createTypedArray(d,2*d+2*c);d/=2;var f=0;for(b=0;b<d;++b)p[f++]=b,p[f++]=(b+1)%d,p[f++]=b+d,p[f++]=(b+1)%d+d;if(0<c)for(var m=Math.min(c,d),h=Math.round(d/m),y=Math.min(h*c,d),b=0;b<y;b+=h)p[f++]=b,p[f++]=b+d;return{boundingSphere:l,attributes:u,indices:p}}(u)):(s=function(e){var t=e.center;l=g.Cartesian3.multiplyByScalar(e.ellipsoid.geodeticSurfaceNormal(t,l),e.height,l),l=g.Cartesian3.add(t,l,l);for(var i=new x.BoundingSphere(l,e.semiMajorAxis),r=L.EllipseGeometryLibrary.computeEllipsePositions(e,!1,!0).outerPositions,a=new M.GeometryAttributes({position:new v.GeometryAttribute({componentDatatype:E.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:L.EllipseGeometryLibrary.raisePositionsToHeight(r,e,!1)})}),n=r.length/3,o=C.IndexDatatype.createTypedArray(n,2*n),s=0,u=0;u<n;++u)o[s++]=u,o[s++]=(u+1)%n;return{boundingSphere:i,attributes:a,indices:o}}(u),A.defined(e._offsetAttribute)&&(a=s.attributes.position.values.length,n=new Uint8Array(a/3),o=e._offsetAttribute===G.GeometryOffsetAttribute.NONE?0:1,G.arrayFill(n,o),s.attributes.applyOffset=new v.GeometryAttribute({componentDatatype:E.ComponentDatatype.UNSIGNED_BYTE,componentsPerAttribute:1,values:n}))),new v.Geometry({attributes:s.attributes,indices:s.indices,primitiveType:v.PrimitiveType.LINES,boundingSphere:s.boundingSphere,offsetAttribute:e._offsetAttribute})}},e.EllipseOutlineGeometry=f});
