define(["./when-54c2dc71","./Check-6c0211bc","./Math-fc8cecf5","./Cartesian2-bddc1162","./Transforms-43808565","./RuntimeError-2109023a","./WebGLConstants-76bb35d1","./ComponentDatatype-6d99a1ee","./GeometryAttribute-49698167","./GeometryAttributes-4fcfcf40","./AttributeCompression-9fc99391","./GeometryPipeline-ee01eec4","./EncodedCartesian3-e9c71cf0","./IndexDatatype-53503fee","./IntersectionTests-60a97ecf","./Plane-c946480f","./GeometryInstance-e56e7f4f","./arrayRemoveDuplicates-ebc732b0","./EllipsoidTangentPlane-9a2d3381","./OrientedBoundingBox-9c6c4bfe","./CoplanarPolygonGeometryLibrary-d7c9456b","./ArcType-dc1c5aee","./EllipsoidRhumbLine-c704bf4c","./PolygonPipeline-c425d4a8","./PolygonGeometryLibrary-818ff8da"],function(i,e,t,l,p,r,n,s,u,d,o,m,a,f,c,y,g,b,h,P,G,v,L,C,T){"use strict";function E(e){var t=(e=i.defaultValue(e,i.defaultValue.EMPTY_OBJECT)).polygonHierarchy;this._polygonHierarchy=t,this._workerName="createCoplanarPolygonOutlineGeometry",this.packedLength=T.PolygonGeometryLibrary.computeHierarchyPackedLength(t)+1}E.fromPositions=function(e){return new E({polygonHierarchy:{positions:(e=i.defaultValue(e,i.defaultValue.EMPTY_OBJECT)).positions}})},E.pack=function(e,t,r){return r=i.defaultValue(r,0),t[r=T.PolygonGeometryLibrary.packPolygonHierarchy(e._polygonHierarchy,t,r)]=e.packedLength,t};var k={polygonHierarchy:{}};return E.unpack=function(e,t,r){t=i.defaultValue(t,0);var n=T.PolygonGeometryLibrary.unpackPolygonHierarchy(e,t);t=n.startingIndex,delete n.startingIndex;var o=e[t];return i.defined(r)||(r=new E(k)),r._polygonHierarchy=n,r.packedLength=o,r},E.createGeometry=function(e){var t=e._polygonHierarchy,r=t.positions;if(!((r=b.arrayRemoveDuplicates(r,l.Cartesian3.equalsEpsilon,!0)).length<3)&&G.CoplanarPolygonGeometryLibrary.validOutline(r)){var n=T.PolygonGeometryLibrary.polygonOutlinesFromHierarchy(t,!1);if(0!==n.length){for(var o=[],i=0;i<n.length;i++){var a=new g.GeometryInstance({geometry:function(e){for(var t=e.length,r=new Float64Array(3*t),n=f.IndexDatatype.createTypedArray(t,2*t),o=0,i=0,a=0;a<t;a++){var c=e[a];r[o++]=c.x,r[o++]=c.y,r[o++]=c.z,n[i++]=a,n[i++]=(a+1)%t}var y=new d.GeometryAttributes({position:new u.GeometryAttribute({componentDatatype:s.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:r})});return new u.Geometry({attributes:y,indices:n,primitiveType:u.PrimitiveType.LINES})}(n[i])});o.push(a)}var c=m.GeometryPipeline.combineInstances(o)[0],y=p.BoundingSphere.fromPoints(t.positions);return new u.Geometry({attributes:c.attributes,indices:c.indices,primitiveType:c.primitiveType,boundingSphere:y})}}},function(e,t){return i.defined(t)&&(e=E.unpack(e,t)),e._ellipsoid=l.Ellipsoid.clone(e._ellipsoid),E.createGeometry(e)}});
