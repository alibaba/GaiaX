import * as chai from 'chai';
import GXExpression from '../../src/gaiax/GXExpression';

const expect = chai.expect;
describe('GXExpression', () => {

  it('raw boolean', () => {
    const data = {};
    expect(GXExpression.desireData(true, data)).to.equal(true);
    expect(GXExpression.desireData(false, data)).to.equal(false);
  });

  it('raw number', () => {
    const data = {};
    expect(GXExpression.desireData(10, data)).to.equal(10);
    expect(GXExpression.desireData(10.1, data)).to.equal(10.1);
  });

  it('GXSelf', () => {
    const data = {};
    expect(GXExpression.desireData('$$', data)).to.equal(data);
  });

  it('GXNull', () => {
    const data = {};
    expect(GXExpression.desireData('null', data)).to.equal(null);
    expect(GXExpression.desireData(null, data)).to.equal(null);
  });

  it('GXBool', () => {
    const data = {};
    expect(GXExpression.desireData('true', data)).to.equal(true);
    expect(GXExpression.desireData('false', data)).to.equal(false);
  });

  it('GXNumber', () => {
    const data = {};
    expect(GXExpression.desireData('10', data)).to.equal(10);
    expect(GXExpression.desireData('10.01', data)).to.equal(10.01);
  });

  it('GXString', () => {
    const data = {};
    expect(GXExpression.desireData('\'GaiaX\'', data)).to.equal('GaiaX');
    expect(GXExpression.desireData('\'GaiaX\' ', data)).to.equal('GaiaX');
  });

  it('GXTextValue', () => {
    const data = {
      title: "X"
    };
    expect(GXExpression.desireData('Gaia + X', data)).to.equal('GaiaX');
    expect(GXExpression.desireData('Gaia + \${title}', data)).to.equal('GaiaX');
  });

  it('GXValue', () => {
    const data = {
      title: "GaiaX1",
      data: {
        title: "GaiaX2"
      },
      nodes: [
        {
          title: "GaiaX3",
          data: {
            title: "GaiaX4"
          },
        }
      ]
    };
    expect(GXExpression.desireData('\${title}', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('\${data.title}', data)).to.equal('GaiaX2');
    expect(GXExpression.desireData('\${nodes[0].title}', data)).to.equal('GaiaX3');
    expect(GXExpression.desireData('\${nodes[0].data.title}', data)).to.equal('GaiaX4');
  });

  it('GXTernaryValue1', () => {
    const data = {

    };
    expect(GXExpression.desireData('@{ true ? \'GaiaX1\' : \'GaiaX2\' }', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('@{ false ? \'GaiaX1\' : \'GaiaX2\' }', data)).to.equal('GaiaX2');

    expect(GXExpression.desireData('@{ true ? true : false }', data)).to.equal(true);
    expect(GXExpression.desireData('@{ false ? true : false }', data)).to.equal(false);

    expect(GXExpression.desireData('@{ 1 ? \'GaiaX1\' : \'GaiaX2\' }', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('@{ 0 ? \'GaiaX1\' : \'GaiaX2\' }', data)).to.equal('GaiaX2');

    expect(GXExpression.desireData('@{ \'true\' ? \'GaiaX1\' : \'GaiaX2\' }', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('@{ \'\' ? \'GaiaX1\' : \'GaiaX2\' }', data)).to.equal('GaiaX2');
  });

  it('GXTernaryValue2', () => {
    const data = {

    };
    expect(GXExpression.desireData('@{ \'GaiaX1\' ?: \'GaiaX2\' }', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('@{ \'\' ?: \'GaiaX2\' }', data)).to.equal('GaiaX2');
  });

  it('GXTernaryValue3-GXTernaryValue2', () => {
    const data = {

    };
    expect(GXExpression.desireData('@{ true ?: @{ true ? GaiaX1 : GaiaX2 } }', data)).to.equal(true);
    expect(GXExpression.desireData('@{ false ?: @{ true ? GaiaX1 : GaiaX2 } }', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('@{ false ?: @{ false ? GaiaX1 : GaiaX2 } }', data)).to.equal('GaiaX2');
  });

  it('GXTernaryValue3-GXTernaryValue1', () => {
    const data = {

    };
    expect(GXExpression.desireData('@{ true ? GaiaX : @{ true ? GaiaX1 : GaiaX2 } }', data)).to.equal('GaiaX');
    expect(GXExpression.desireData('@{ false ? GaiaX : @{ true ? GaiaX1 : GaiaX2 } }', data)).to.equal('GaiaX1');
    expect(GXExpression.desireData('@{ false ? GaiaX : @{ false ? GaiaX1 : GaiaX2 } }', data)).to.equal('GaiaX2');
  });

  it('GXText', () => {
    const data = {
    };
    expect(GXExpression.desireData('title', data)).to.equal('title');
  });

  it('GXEval', () => {
    const data = {
      data: {
      },
      nodes: [
        {
          data: {
            tag: true
          },
        }
      ]
    };
    expect(GXExpression.desireData('eval( 10 == 8 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10 == 10 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( \'GaiaX\' == \'GaiaX\' )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( \'GaiaX1\' == \'GaiaX2\' )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( true == true )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( true == false )', data)).to.equal(false);

    expect(GXExpression.desireData('eval( 10 >= 8 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10 >= 10 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10 >= 11 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10.8 >= 10.1 )', data)).to.equal(true);

    expect(GXExpression.desireData('eval( 10 > 8 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10 > 10 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10 > 11 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10.8 > 10.1 )', data)).to.equal(true);

    expect(GXExpression.desireData('eval( 10 <= 8 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10 <= 10 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10 <= 11 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10.8 <= 10.1 )', data)).to.equal(false);

    expect(GXExpression.desireData('eval( 10 < 8 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10 < 10 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10 < 11 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10.8 < 10.1 )', data)).to.equal(false);

    expect(GXExpression.desireData('eval( 10 != 8 )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( 10 != 10 )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( 10.8 != 10.1 )', data)).to.equal(true);

    expect(GXExpression.desireData('eval( true && true )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( true && false )', data)).to.equal(false);
    expect(GXExpression.desireData('eval( false && false )', data)).to.equal(false);

    expect(GXExpression.desireData('eval( true || true )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( true || false )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( false || false )', data)).to.equal(false);


    expect(GXExpression.desireData('eval( ${nodes[0].data.tag} && true )', data)).to.equal(true);
    expect(GXExpression.desireData('eval( ${nodes[0].data.tag} || false )', data)).to.equal(true);
  });


  it('GXJson', () => {
    const data = {
      title: "GaiaX1",
      data: {
        title: "GaiaX2"
      },
      nodes: [
        {
          title: "GaiaX3",
          data: {
            title: "GaiaX4"
          },
        }
      ]
    };

    expect(GXExpression.desireData({
      title: "GaiaX",
      title1: "${title}",
      title2: "${data.title}",
      title3: "${nodes[0].title}",
      title4: "${nodes[0].data.title}",
    }, data).title).to.equal('GaiaX');

    expect(GXExpression.desireData({
      title: "GaiaX",
      title1: "${title}",
      title2: "${data.title}",
      title3: "${nodes[0].title}",
      title4: "${nodes[0].data.title}",
    }, data).title1).to.equal('GaiaX1');

    expect(GXExpression.desireData({
      title: "GaiaX",
      title1: "${title}",
      title2: "${data.title}",
      title3: "${nodes[0].title}",
      title4: "${nodes[0].data.title}",
    }, data).title2).to.equal('GaiaX2');

    expect(GXExpression.desireData({
      title: "GaiaX",
      title1: "${title}",
      title2: "${data.title}",
      title3: "${nodes[0].title}",
      title4: "${nodes[0].data.title}",
    }, data).title3).to.equal('GaiaX3');

    expect(GXExpression.desireData({
      title: "GaiaX",
      title1: "${title}",
      title2: "${data.title}",
      title3: "${nodes[0].title}",
      title4: "${nodes[0].data.title}",
    }, data).title4).to.equal('GaiaX4');

  });
});