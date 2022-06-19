import * as chai from 'chai';
import computeValuePath from '../../src/gaiax/GXValuePath';

const expect = chai.expect;
describe('GXValuePath', () => {

  it('normal', () => {
    const data = {
      title: 'GaiaX',
      data: {
        title: 'GaiaX1'
      }
    };
    expect(computeValuePath('data.title', data)).to.equal('GaiaX1');
  });

  it('array', () => {
    const data = {
      title: 'GaiaX',
      nodes: [
        {
          title: 'GaiaX1',
          data: {
            title: 'GaiaX2'
          }
        }
      ]
    };
    expect(computeValuePath('nodes[0].data.title', data)).to.equal('GaiaX2');
  });
});