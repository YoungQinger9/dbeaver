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
import java.util.Collection;
import java.util.Collections;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.oracle.model.OraclePrivTable;
import org.jkiss.dbeaver.ext.oracle.model.OracleSchema;
import org.jkiss.dbeaver.ext.oracle.model.OracleTable;
import org.jkiss.dbeaver.ext.oracle.model.OracleTablespace;
import org.jkiss.dbeaver.model.meta.LazyProperty;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

/**
 * YashanDBTable
 */
public class YashanDBTable extends OracleTable {

	public YashanDBTable(DBRProgressMonitor monitor, OracleSchema schema, ResultSet dbResult) {
		super(monitor, schema, dbResult);
	}

	public YashanDBTable(OracleSchema schema, String name) {
		super(schema, name);
	}

	@Override
	public Collection<OraclePrivTable> getTablePrivs(DBRProgressMonitor monitor) throws DBException {
		// YashanDB not support yet
		return Collections.emptyList();
	}

	@Override
	@Property(viewable = true, order = 22, editable = true, updatable = false, listProvider = TablespaceListProvider.class)
	@LazyProperty(cacheValidator = OracleTablespace.TablespaceReferenceValidator.class)
	public Object getTablespace(DBRProgressMonitor monitor) throws DBException {
		// YashanDB not support move table space
		return OracleTablespace.resolveTablespaceReference(monitor, this, null);
	}

}
