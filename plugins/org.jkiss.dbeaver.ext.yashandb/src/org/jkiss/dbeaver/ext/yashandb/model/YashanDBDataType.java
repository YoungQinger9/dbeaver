/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2025 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.yashandb.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.oracle.model.OracleDataType;
import org.jkiss.dbeaver.ext.oracle.model.OracleUtils;
import org.jkiss.dbeaver.model.DBPDataKind;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;

/**
 * YashanDBDataType
 */
public class YashanDBDataType extends OracleDataType {

	static final Map<String, TypeDesc> PREDEFINED_TYPES = new HashMap<>();

	static {
		// YashanDB custom
		// Numeric type
		PREDEFINED_TYPES.put("TINYINT", new TypeDesc(DBPDataKind.NUMERIC, Types.TINYINT, 63, 127, -84));
		PREDEFINED_TYPES.put("SMALLINT", new TypeDesc(DBPDataKind.NUMERIC, Types.SMALLINT, 63, 127, -84));
		PREDEFINED_TYPES.put("INTEGER", new TypeDesc(DBPDataKind.NUMERIC, Types.INTEGER, 63, 127, -84));
		PREDEFINED_TYPES.put("BIGINT", new TypeDesc(DBPDataKind.NUMERIC, Types.BIGINT, 63, 127, -84));

		PREDEFINED_TYPES.put("FLOAT", new TypeDesc(DBPDataKind.NUMERIC, Types.FLOAT, 63, 127, -84));
		PREDEFINED_TYPES.put("DOUBLE", new TypeDesc(DBPDataKind.NUMERIC, Types.DOUBLE, 63, 127, -84));

		PREDEFINED_TYPES.put("NUMBER", new TypeDesc(DBPDataKind.NUMERIC, Types.NUMERIC, 18, 0, 127));

		PREDEFINED_TYPES.put("BIT", new TypeDesc(DBPDataKind.NUMERIC, Types.BIT, 64, 0, 0));

		// String type
		PREDEFINED_TYPES.put("CHAR", new TypeDesc(DBPDataKind.STRING, Types.CHAR, 0, 0, 0));
		PREDEFINED_TYPES.put("NCHAR", new TypeDesc(DBPDataKind.STRING, Types.CHAR, 0, 0, 0));
		PREDEFINED_TYPES.put("VARCHAR", new TypeDesc(DBPDataKind.STRING, Types.VARCHAR, 0, 0, 0));
		PREDEFINED_TYPES.put("NVARCHAR", new TypeDesc(DBPDataKind.STRING, Types.NVARCHAR, 0, 0, 0));

		// Boolean type
		PREDEFINED_TYPES.put("BOOLEAN", new TypeDesc(DBPDataKind.BOOLEAN, Types.BOOLEAN, 0, 0, 0));

		// Date type
		PREDEFINED_TYPES.put("DATE", new TypeDesc(DBPDataKind.DATETIME, Types.DATE, 0, 0, 0));

		PREDEFINED_TYPES.put("TIME", new TypeDesc(DBPDataKind.DATETIME, Types.TIME, 0, 0, 0));

		PREDEFINED_TYPES.put("TIMESTAMP", new TypeDesc(DBPDataKind.DATETIME, Types.TIMESTAMP, 0, 0, 0));

		PREDEFINED_TYPES.put("TIMESTAMP WITH LOCAL TIME ZONE",
				new TypeDesc(DBPDataKind.DATETIME, Types.TIMESTAMP_WITH_TIMEZONE, 0, 0, 0));

		PREDEFINED_TYPES.put("TIMESTAMP WITH TIME ZONE",
				new TypeDesc(DBPDataKind.DATETIME, Types.TIMESTAMP_WITH_TIMEZONE, 0, 0, 0));

		PREDEFINED_TYPES.put("INTERVAL YEAR TO MONTH", new TypeDesc(DBPDataKind.STRING, Types.VARCHAR, 0, 0, 0));
		PREDEFINED_TYPES.put("INTERVAL DAY TO SECOND", new TypeDesc(DBPDataKind.STRING, Types.VARCHAR, 0, 0, 0));

		// Big object type
		PREDEFINED_TYPES.put("BLOB", new TypeDesc(DBPDataKind.CONTENT, Types.BLOB, 0, 0, 0));
		PREDEFINED_TYPES.put("CLOB", new TypeDesc(DBPDataKind.CONTENT, Types.CLOB, 0, 0, 0));
		PREDEFINED_TYPES.put("NCLOB", new TypeDesc(DBPDataKind.CONTENT, Types.NCLOB, 0, 0, 0));

		// Others
		PREDEFINED_TYPES.put("RAW", new TypeDesc(DBPDataKind.BINARY, Types.VARBINARY, 0, 0, 0));
		PREDEFINED_TYPES.put("JSON", new TypeDesc(DBPDataKind.CONTENT, Types.BLOB, 0, 0, 0));
		PREDEFINED_TYPES.put("XMLTYPE", new TypeDesc(DBPDataKind.CONTENT, Types.CLOB, 0, 0, 0));
		PREDEFINED_TYPES.put("BFILE", new TypeDesc(DBPDataKind.CONTENT, Types.OTHER, 0, 0, 0));
		PREDEFINED_TYPES.put("ROWID", new TypeDesc(DBPDataKind.ROWID, Types.ROWID, 0, 0, 0));
		PREDEFINED_TYPES.put("UROWID", new TypeDesc(DBPDataKind.BINARY, Types.BINARY, 0, 0, 0));

		PREDEFINED_TYPES.put("UNKNOWN", new TypeDesc(DBPDataKind.UNKNOWN, 0, 0, 0, 0));
	}

	public YashanDBDataType(DBSObject owner, ResultSet dbResult) {
		super(owner, dbResult);
		this.methodCache = this.hasMethods ? new YashanDBMethodCache() : null;
	}

	public YashanDBDataType(DBSObject owner, String typeName, boolean persisted) {
		super(owner, typeName, persisted);
		this.methodCache = new YashanDBMethodCache();
	}

	@NotNull
	@Override
	@Property(hidden = true, editable = true, updatable = true, order = -1)
	public String getObjectDefinitionText(@NotNull DBRProgressMonitor monitor, @NotNull Map<String, Object> options)
			throws DBCException {
		if (flagPredefined) {
			return "-- Source code not available";
		}
		if (sourceDeclaration == null && monitor != null) {
			sourceDeclaration = YashanDBUtils.getSource(monitor, this, false, true);
		}
		return sourceDeclaration;
	}

	@NotNull
	@Override
	@Property(hidden = true, editable = true, updatable = true, order = -1)
	public String getExtendedDefinitionText(@NotNull DBRProgressMonitor monitor) throws DBException {
		if (sourceDefinition == null && monitor != null) {
			sourceDefinition = YashanDBUtils.getSource(monitor, this, true, false);
		}
		return sourceDefinition;
	}

	class YashanDBMethodCache extends MethodCache {

		@NotNull
		@Override
		protected JDBCStatement prepareObjectsStatement(@NotNull JDBCSession session, @NotNull OracleDataType owner)
				throws SQLException {
			// YashanDB custom
			final JDBCPreparedStatement dbStat = session.prepareStatement(
					"SELECT *" + " FROM " + OracleUtils.getSysSchemaPrefix(getDataSource()) + "ALL_TYPE_METHODS "
							+ " m\n" + "WHERE m.OWNER=? AND m.TYPE_NAME=?\n" + "ORDER BY m.METHOD_NO");
			dbStat.setString(1, YashanDBDataType.this.parent.getName());
			dbStat.setString(2, getName());
			return dbStat;
		}

		@Override
		protected YashanDBDataTypeMethod fetchObject(@NotNull JDBCSession session, @NotNull OracleDataType owner,
				@NotNull JDBCResultSet resultSet) throws SQLException, DBException {
			return new YashanDBDataTypeMethod(session.getProgressMonitor(), YashanDBDataType.this, resultSet);
		}
	}
}
